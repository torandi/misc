<?php
class Bostad {
	public static function available_lans() {
		global $db;
		$stmt = $db->prepare("select distinct lan from bostader");
		$stmt->bind_result($lan);
		$stmt->execute();
		$lans = array(""=>"Alla");
		while($stmt->fetch()) {
			$lans[$lan] = $lan;
		}
		$stmt->close();
		return $lans;
	}

	public static function objekttyper() {
		global $db;
		$stmt = $db->prepare("select distinct objekttyp from bostader");
		$stmt->bind_result($objekttyp);
		$stmt->execute();
		$objekttyper = array(""=>"Alla");
		while($stmt->fetch()) {
			$objekttyper[$objekttyp] = $objekttyp;
		}
		$stmt->close();
		return $objekttyper;
	}

	public static function antal_rum() {
		global $db;
		$stmt = $db->prepare("select distinct rum from bostader");
		$stmt->bind_result($rum);
		$stmt->execute();
		$antal_rum = array();
		while($stmt->fetch()) {
			$antal_rum[] = $rum;
		}
		$stmt->close();
		return $antal_rum;
	}
}
