<?php

function request($var, $default=NULL) {
	if(isset($_REQUEST[$var])) 
		return $_REQUEST[$var];
	else
		return $default;
}

function get($var, $default=NULL) {
	if(isset($_GET[$var])) 
		return $_GET[$var];
	else
		return $default;
}

function post($var, $default=NULL) {
	if(isset($_POST[$var])) 
		return $_POST[$var];
	else
		return $default;
}

function redirect($url) {
	header("Location: $url");
}

function output_json($data) {
	header("Content-Type: text/json");
	echo json_encode($data);
	exit();
}

function ends_with($haystack, $needle) {
	if(strlen($needle) == 0) return true;

	return (substr($haystack, -strlen($needle)) === $needle);
}

function starts_with($haystack, $needle) {
	return !strncmp($haystack, $needle, strlen($needle));
}
