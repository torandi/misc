<?

function range_field($form_name, $name, $label) {
	return "
		<div class='control-group'>
			<label class='control-label' for='{$form_name}_$name'>$label</label>
			<div class='controls'>
				<input type='number' id='{$form_name}_$name' name='{$name}_min' class='input-mini' style='text-align: right'/> till
				<input type='number' name='{$name}_max' class='input-mini' style='text-align: right'/>
			</div>
		</div>
	";
}

function field($form_name, $name, $label, $type) {
	return "
		<div class='control-group'>
			<label class='control-label' for='{$form_name}_$name'>$label: </label>
			<div class='controls'>
				<input type='$type' id='{$form_name}_$name' name='$name'/>
			</div>
		</div>";
}

function textarea($form_name, $name, $label) {
	return "
		<div class='control-group'>
			<label class='control-label' for='{$form_name}_$name'>$label: </label>
			<div class='controls'>
				<textarea id='{$form_name}_$name' name='$name'/></textarea>
			</div>
		</div>";
}

/**
 * options:
 *		array(
 *			'value' => 'Label'
 *		)
 */
function radio_select($form_name, $name, $label, $options) {
	$option_html="";
	$first = "checked='checked'";
	foreach($options as $value => $option_label) {
		$option_html .= "
		<label class='radio inline'>
			<input type='radio' $first class='radio inline' value='$value' name='$name'/>
			$option_label
		</label>";
		$first = "";
	}
	return "
		<div class='control-group'>
			<label class='control-label' for='{$form_name}_$name'>$label</label>
			<div class='controls'>$option_html
			</div>
		</div>
	";
}

/**
 * options:
 *		array(
 *			'value' => 'Label'
 *		)
 */
function select($form_name, $name, $label, $options) {
	$option_html="";
	foreach($options as $value => $option_label) {
		$option_html .= "
			<option value='$value'>$option_label</option>";
	}
	return "
		<div class='control-group'>
			<label class='control-label' for='{$form_name}_$name'>$label</label>
			<div class='controls'>
			<select name='$name' id='{$form_name}_$name'>$option_html</select>
			</div>
		</div>
	";
}

function range_select($form_name, $name, $label, $options) {
	$option_html="";
	foreach($options as $value) {
		$option_html .= "
			<option value='$value'>$value</option>";
	}
	return "
		<div class='control-group'>
			<label class='control-label' for='{$form_name}_$name'>$label</label>
			<div class='controls'>
			<select name='{$name}_min' class='input-mini min' style='text-align: right' id='{$form_name}_{$name}_min'>$option_html</select>
			till
			<select name='{$name}_max' class='input-mini max' style='text-align: right' id='{$form_name}_{$name}_max'>$option_html</select>
			</div>
		</div>
	";
}
