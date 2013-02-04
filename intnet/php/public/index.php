<?

include "../includes.php";
include "../form_helpers.php";

?>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml" xml:lang="sv" lang="sv">
	<head>
		<meta http-equiv="content-type" content="text/html; charset=utf-8" />
		<title>Bostassök</title>
		<script src="js/jquery.js" type="text/javascript"></script>
		<script src="js/bostad.js" type="text/javascript"></script>
		<link type="text/css" rel="stylesheet" href="bootstrap.min.css" />
		<link type="text/css" rel="stylesheet" href="bostad.css" />
	</head>
	<body>
		<div class"row" style="margin-top: 10px">
			<div class="span12">
				<div class="page-header">
					<h1>Bostadssök</h1>
				</div>

				<div class="well">
					<h4>Sök bostad</h4>
					<form id="search" name="search" class="form-horizontal">
						<?=select("search", "lan", "Län", Bostad::available_lans())?>
						<?=select("search", "objekttyp", "Objekttyp", Bostad::objekttyper())?>
						<?=range_select("search", "rum", "Antal rum", Bostad::antal_rum())?>
						<input type="submit" class="btn btn-primary" value="Sök"/>
					</form>
				</div>
				<table class="table table-stripped sortable">
					<thead>
						<tr>
							<th class="lan">Län</th>
							<th class="adress">Adress</th>
							<th class="rum">Rum</th>
							<th class="area">Boarea</th>
							<th class="pris">Pris</th>
							<th class="avgift">Avgift/hyra</th>
							<th class="objekttyp">Typ</th>
						</tr>
					</thead>
					<tbody id="results">
					</tbody>
				</table>
		</div>
	</body>
</html>
