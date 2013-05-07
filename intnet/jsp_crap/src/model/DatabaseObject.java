package model;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;

public abstract class DatabaseObject<T extends DatabaseObject<T>> {
	protected Integer id = null;
	protected HashMap<String, Object> values;
	protected ArrayList<Field> columns;
	
	protected abstract Class<T> cls();
	protected abstract String table_name();
	protected String id_name() { return "id"; };

	public DatabaseObject(){
		columns = new ArrayList<Field>();
		values = new HashMap<String, Object>();
		DatabaseMetaData meta = DatabaseConnection.get().getMetaData();
		try {
			ResultSet rs = meta.getColumns(null, null, table_name(), null);
			while(rs.next()) {
				Field f = new Field(rs.getString("COLUMN_NAME"), rs.getInt("DATA_TYPE"));
				columns.add(f);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	public boolean commit() {
		try {
			String query;
			if(id == null) {
				query = "insert into `"+table_name()+"` SET ";
			} else {
				query = "update `"+table_name()+"` SET ";
			}
			for(Field f : columns) {
				if(!f.name.equals(id_name())) {
					query +="`"+f.name+"` = ?,";
				}
			}
			query = query.substring(0, query.length() - 1);
			if(id != null) {
				query += "where `"+id_name()+"` = ?";
			}
			PreparedStatement stmt = DatabaseConnection.get().prepareStatement(query);
			int index = 1;
			for(Field f : columns) {
				if(!f.name.equals(id_name())) {
					stmt.setObject(index++, get(f.name), f.type);
				}
			}
			if(id != null) {
				stmt.setInt(index, id);
			}
			return stmt.execute();
		} catch (SQLException e) {
			System.err.println("Commit failed");
			e.printStackTrace();
			return false;
		}
	}
	
	public T first(String attr, Object value) {
		try {
			PreparedStatement stmt = statement("`"+ attr + "` = ? limit 1");
			stmt.setObject(1, value);
			ResultSet rs = stmt.executeQuery();
			ResultSetMetaData meta = rs.getMetaData();
			if(rs.first()) {
				T obj = cls().newInstance();
				obj.set_from_db(meta, rs);
				return obj;
			} else {
				return null;
			}
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	
	public T from_id(int id) {
		return first(id_name(), new Integer(id));
	}
	
	public ArrayList<T> find(String attr, Object value) {
		try {
			PreparedStatement stmt = statement("`"+ attr + "` = ?");
			stmt.setObject(1, value);
			return where(stmt);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	
	protected void set_from_db(ResultSetMetaData meta, ResultSet rs) throws SQLException {
		for(int i=1; i<=meta.getColumnCount(); ++i) {
			values.put(meta.getColumnName(i), rs.getObject(i));
		}
		id = (Integer)get(id_name());
	}
	
	public void set(String field, Object value) {
		values.put(field, value);
	}
	
	public Object get(String field) {
		return values.get(field);
	}
	
	protected int get_int(String field) {
		return ((Integer)get(field)).intValue();
	}
	
	public ArrayList<T> where(PreparedStatement stmt) {
		try {
			ResultSet rs = stmt.executeQuery();
			ResultSetMetaData meta = rs.getMetaData();
			ArrayList<T> res = new ArrayList<T>();
			while(rs.next()) {
				T obj = cls().newInstance();
				obj.set_from_db(meta, rs);
				res.add(obj);
			}
			return res;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	
	public PreparedStatement statement(String where) {
		return DatabaseConnection.get().prepareStatement("select * from "+table_name()+" WHERE "+where);
	}
	
	protected static class Field {
		public final String name;
		public final int type;
		
		public Field(String name, int type) {
			this.name = name;
			this.type = type;
		}
		
		public String toString() {
			return name + " ("+type +")";
		}
	}
	
	public int id() {
		return id.intValue();
	}
	
	public String toString() {
		String str = this.getClass().getName() + "{\n";
		for(String k : values.keySet()) {
			str += "\t"+k+"="+values.get(k)+"\n";
		}
		str += "}";
		return str;
	}
}
