shell.run("disk/utils")

local control_url = "http://langwell.nsg.cc/digger/index.php"

smart = {
    pos = vector.new(0, 0, 0),
    dir = vector.new(0, 1, 0),
    id = null
}

smart.__index = smart

function smart.clone_pos()
    return vcopy(smart.pos);
end

function smart.clone_dir()
    return vcopy(smart.dir);
end

function smart.forward(times, dig)
    times = times or 1
    for i = 1, times, 1 do
        local tries = 20
        while(not turtle.forward() and dig) do
            turtle.dig()
            tries = tries - 1;
            if(tries < 0) then return false end
        end
        
        smart.pos = smart.pos + smart.dir;
    end
    return true
end

function smart.back(times)
    times = times or 1
    for i = 1, times, 1 do
        if(turtle.back()) then
            smart.pos = smart.pos - smart.dir;
        else
            return false;
        end
    end
    return true
end

function smart.up(times, dig)
    times = times or 1
    for i = 1, times, 1 do
        local tries = 20
        while(not turtle.up() and dig) do
            turtle.digUp()
            tries = tries - 1;
            if(tries < 0) then return false end
        end
        
        smart.pos.z = smart.pos.z + 1;
    end
    return true
end


function smart.down(times, dig)
    times = times or 1
    for i = 1, times, 1 do
        local tries = 20
        while(not turtle.down() and dig) do
            turtle.digDown()
            tries = tries - 1;
            if(tries < 0) then return false end
        end
        
        smart.pos.z = smart.pos.z - 1;
    end
    return true
end

function smart.turnLeft()
    if(smart.dir.x == 0) then
        if(smart.dir.y == 1) then
            smart.dir = vector.new(-1, 0)
        else
            smart.dir = vector.new(1 ,0)
        end
    elseif(smart.dir.x == 1) then
        smart.dir = vector.new(0, 1)
    else -- x == -1
        smart.dir = vector.new(0 ,-1)
    end
    turtle.turnLeft();
end

function smart.turnRight()
    if(smart.dir.x == 0) then
        if(smart.dir.y == 1) then
            smart.dir = vector.new(1, 0)
        else
            smart.dir = vector.new(-1 ,0)
        end
    elseif(smart.dir.x == 1) then
        smart.dir = vector.new(0, -1)
    else -- x == -1
        smart.dir = vector.new(0 ,1)
    end
    turtle.turnRight();
end

function smart.check_right()
    turtle.turnRight();
    local detect = turtle.detect();
    turtle.turnLeft();
    return detect;
end


function smart.check_left()
    turtle.turnLeft();
    local detect = turtle.detect();
    turtle.turnRight();
    return detect;
end

function smart.axis_align(axis, direction)
    if(direction == 0) then return end
    
    if(axis == "x") then
        if(smart.dir.x == 0) then
            if(smart.dir.y == direction) then
                smart.turnRight();
            else
                smart.turnLeft();
            end
        else
            if(direction ~= smart.dir.x) then
                smart.turnLeft();
                smart.turnLeft();
            end
        end
    elseif(axis == "y") then
        if(smart.dir.y == 0) then
            if(smart.dir.x == direction) then
                smart.turnLeft();
            else
                smart.turnRight();
            end
        else
            if(direction ~= smart.dir.y) then
                smart.turnLeft();
                smart.turnLeft();
            end
        end
    elseif(axis == "z") then
        -- ignore, nothing to rotate to
    else
        print("Unknown axis " .. axis);
    end 
end

function smart.go_axis(axis, delta, dig)
    local f = null
    if(delta == 0) then return end
    
    if(axis == "z") then
        if(delta < 0) then
            return smart.down(math.abs(delta), dig);
        else
            return smart.up(delta, dig);
        end
    else
        smart.axis_align(axis, sgn(delta));
        smart.forward(math.abs(delta), dig);
    end
end

-- rotate to direction (must be axis aligned)
function smart.rotate_to(direction) 
    if(direction.x == 0) then
        smart.axis_align(direction.x)
    else
        smart.axis_align(direction.y)
    end
end

function smart.drop(num_slots)
    for i = 1, num_slots, 1 do
        if(not smart.slot_empty(i)) then 
            turtle.select(i)
            turtle.drop()
        end
    end
end

function smart.slot_empty(slot) 
    return turtle.getItemCount(slot) == 0
end

-- @all: set to match all, default: match any
function smart.compare(first_slot, last_slot, all)
    for i = first_slot, last_slot, 1 do
        turtle.select(i)
        if(turtle.compare()) then
            if(not all) then return i end
        else 
            if(all) then return false end
        end
    end
    return all or false
end

-- @all: set to match all, default: match any
function smart.compareUp(first_slot, last_slot, all)
    for i = first_slot, last_slot, 1 do
        turtle.select(i)
        if(turtle.compareUp()) then
            if(not all) then return i end
        else 
            if(all) then return false end
        end
    end
    return all or false
end

-- @all: set to match all, default: match any
function smart.compareDown(first_slot, last_slot, all)
    for i = first_slot, last_slot, 1 do
        turtle.select(i)
        if(turtle.compareDown()) then
            if(not all) then return i end
        else 
            if(all) then return false end
        end
    end
    return all or false
end

function smart.any_free_slot(first_slot, last_slot)
    for i = first_slot, last_slot, 1 do
        if(smart.slot_empty(i)) then
            return true
        end
    end
    return false
end

-- axis_order: optional order of proccessing
function smart.goto(target, axis_order, dig)
    axis_order = axis_order or {"x", "y", "z"}
    for _, axis in ipairs(axis_order) do
        local diff = target[axis] - smart.pos[axis]
        smart.go_axis(axis, diff, dig);
    end
end

-- robot controll reporting

function smart.generate_id()
	smart.id = string.format("torandi-%d", os.getComputerID())
end

function post_report(status)
	status = status or ""
	
	local msg = string.format("Position: %s, status: %s", smart.pos:tostring(), status)
	local res = http.post(control_url, string.format("id=%s&data=%s", smart.id, textutils.urlEncode(msg)))
   	if(not res) then
		print("Warning! Can't reach robot control!")
		return ""
	else
		local cmd = res.readLine();
		res.close()
		return cmd
	end
end

function smart.report(status)
	if(not smart.id) then
		smart.generate_id()
	end

	local cmd = post_report(status)
	
	if(cmd == "stop") then
		post_report("Received stop (old status: " .. status .. ")");
		return false
	else
		return true
	end
end