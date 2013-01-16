var player1 = {
	name: "Player1",
	score: 0
}
var player2 = {
	name: "Player2",
	score: 0
}

var set = null;
var size = [0, 0];

var sets = {
	"landscapes": {
		path: "images/landscapes/",
		size: 17
	}
}

var board = null;

var unknown_path = null;
var unknown_hover_path = null;

$(function() {
	$("#start").click(function() {
		player1.name = $("#p1").val();
		player1.score = 0;
		player2.name = $("#p2").val();
		player2.score = 0;
		$("#newgame").slideUp();
		load_set(sets.landscapes, [4, 4]);
		$("#game").slideDown();
		return false;
	})
})

function load_set(_set, _size) {
	set = _set;
	size = _size;

	unknown_path = set.path + "unknown.png";
	unknown_hover_path = set.path + "unknown_hover.png";

	$("#p1_name").html(player1.name);
	$("#p2_name").html(player2.name);
	load_board();
	update_score();
}

function update_score() {
	$("#p1_score").html(player1.score);
	$("#p2_score").html(player2.score);
}

function load_board() {
	var possible_cards = [];
	var possible_pos = [];
	var positions = size[0]*size[1];
	board = Array(positions);

	for(i=0;i<set.size; ++i) {
		possible_cards.push(i);
	}
	for(i=0;i<positions; ++i) {
		possible_pos.push(i);
	}

	for(var i=0; i<positions/2; ++i) {
		card_index = random(possible_cards)
		card = possible_cards[card_index];
		possible_cards.splice(card_index, 1);

		pos_index = random(possible_pos)
		pos1 = possible_pos[pos_index];
		possible_pos.splice(pos_index, 1);

		pos_index = random(possible_pos)
		pos2 = possible_pos[pos_index];
		possible_pos.splice(pos_index, 1);

		board[pos1] = new_card(card);
		board[pos2] = new_card(card);
		console.log("Card "+ card + " to " + pos1 + " and " + pos2);
	}

	//Generate html for board:
	generate_board_html();
}

function random(from) {
	index = Math.floor(Math.random() * from.length);
	return index;
}


function new_card(card_id) {
	return {
		id: card_id,
		path: set.path + card_id + ".png",
		found: false
	}
}

function generate_board_html() {
	html = ""
	for(y = 0; y < size[1]; ++y) {
		html += "<tr class='row' data-row='" + y + "'>";
		for(x = 0; x < size[0]; ++x) {
			var board_pos = y * size[0] + x;
			var card = board[board_pos];
			html += "<td class='cell' data-cell='" + x + "'> <img src='" + card.path + "' class='card' data-card='" + board_pos + "'/> </td>"
		}
		html +="</tr>"
	}
	$("#board").html(html);
}
