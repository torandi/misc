<?php

include "../includes.php";

$query = "select lan, adress, rum, area, pris, avgift, objekttyp from bostader where 1 = 1 ";
$param_types = "";
$params = array();


foreach($_GET as $field => $value) {
	if(!empty($value)) {
		if(ends_with($field, "_min")) {
			$field = substr($field, 0, -4);
			$query .= "and $field >= ? ";
			$param_types .= "i";
		} else if(ends_with($field, "_max")) {
			$field = substr($field, 0, -4);
			$query .= "and $field <= ? ";
			$param_types .= "i";
		} else if(ends_with($field, "_like")) {
			$field = substr($field, 0, -5);
			$query .= "and lower($field) LIKE lower(?) ";
			$value = "%$value%";
			$param_types .= "s";
		} else {
			$query .= "and $field = ? ";
			$param_types .= "s";
		}
		$params[] = $value;
	}
}

$query .= "order by pris asc";

$stmt = $db->prepare($query);


if($stmt === false) {
	echo "<tr><td colspan='7'>Ett internt fel inträffade: {$db->error}</td></tr>";
	die();
}

/* 
 * Build call array for param
 * must be by reference
 */
$args = array($param_types);
foreach($params as &$p) {
	$args[] = &$p;
}

call_user_func_array(array($stmt, "bind_param"), $args);
if(!$stmt->execute()) {
	die("<tr><td colspan='7'>Ett internt fel inträffade: {$stmt->error}</td></tr>");
}

$stmt->store_result();

$stmt->bind_result($lan, $adress, $rum, $area, $pris, $avgift, $objekttyp);

if($stmt->num_rows < 1) {
?>
	<tr><td colspan="7">Inga bostäder hittades</td></tr>
<?php
} else {
	while($stmt->fetch()) {
?>
	<tr>
		<td class="lan"><?=$lan?></td>
		<td class="adress"><?=$adress?></td>
		<td class="rum" data-numerical-value="<?=$rum?>"><?=$rum?> rok</td>
		<td class="area" data-numerical-value="<?=$area?>"><?=$area?> m²</td>
		<td class="pris" data-numerical-value="<?=$pris?>"><?=$pris?> kr</td>
		<td class="avgift" data-numerical-value="<?=$avgift?>"><?=$avgift?> kr/månad</td>
		<td class="objekttyp"><?=$objekttyp?></td>
	</tr>
<?php
	}
}
$stmt->close();

?>
