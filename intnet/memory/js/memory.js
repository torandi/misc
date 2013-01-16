var players = [
	{
		name: "Player1",
		score: 0,
		old_score: 0,
	},
	{
		name: "Player2",
		score: 0,
		old_score: 0,
	}
]

var current_player = 0;

var set = null;
var size = [0, 0];

var sets = {
	"landscapes": {
		path: "images/landscapes/",
		size: 18
	}
}

var board = null;

var remaining_cards = 0;

var unknown_path = null;
var unknown_hover_path = null;

var open_cards = []

$(function() {
	$("#start").click(function() {
		players[0].name = $("#p1").val();
		players[0].score = 0;
		players[0].old_score = 0;
		players[1].name = $("#p2").val();
		players[1].score = 0;
		players[1].old_score = 0;
		$("#newgame").slideUp();
		var board_size = $("#board_size option:selected")
		load_set(sets.landscapes, [board_size.data("width"), board_size.data("height")]);
		$("#game").slideDown();
		return false;
	})

	$("#restart").click(function() {
		$("#game").slideUp()
		$("#win").fadeOut()
		$("#newgame").slideDown()
		return false
	})
})

function load_set(_set, _size) {
	set = _set;
	size = _size;

	unknown_path = set.path + "unknown.png";
	unknown_hover_path = set.path + "unknown_hover.png";

	$("#p1_name").html(players[0].name);
	$("#p2_name").html(players[1].name);

	current_player = 0;
	load_board();
	update_turn();
	update_score();
}

function update_score() {
	for(i = 0; i<2; ++i) {
		$("#p" + (i + 1 ) + "_score").html(players[i].score);
		if(players[i].score != players[i].old_score) {
			$("#p" + (i + 1 ) + "_score").flash()
			players[i].old_score = players[i].score
		}
	}
}

function update_turn() {
	$("#next_player").html(players[current_player].name);
	$("#next_player").flash();
}

function load_board() {
	var possible_cards = [];
	var possible_pos = [];
	var positions = size[0]*size[1];

	if(positions/2 > set.size) {
		alert("Too large board for this picture set!");
		return;
	}

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
	
	remaining_cards = positions/2;

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
		found: false,
		visible: false
	}
}

function generate_board_html() {
	html = ""
	for(y = 0; y < size[1]; ++y) {
		html += "<tr class='row' data-row='" + y + "'>";
		for(x = 0; x < size[0]; ++x) {
			var board_pos = y * size[0] + x;
			var card = board[board_pos];
			html += "<td class='cell' data-cell='" + x + "'> <div class='card' data-card='" + board_pos + "'> \
				<img src='" + unknown_path + "' class='front'/> \
				<img src='" + card.path + "' class='back'/> \
			</td>"
		}
		html +="</tr>"
	}
	$("#board").html(html);
	register_handlers();
}

$.fn.flip = function() {
	this.each(function() {
		var obj = $(this)
		if(obj.hasClass("flipped")) {
			obj.removeClass("flipped")
		} else {
			$(this).removeClass("peak");
			$(this).children(".front").attr("src", unknown_path);
			obj.addClass("flipped")
		}
	})
}

$.fn.flash = function() {
	this.each(function() {
		var obj = $(this)
		obj.fadeOut(200).fadeIn(200)
	})
}

function register_handlers() {
	$(".card").hover(function() {
		if(!$(this).hasClass("flipped")) {
			$(this).children(".front").attr("src", unknown_hover_path);
			$(this).addClass("peak");
		}
	}, function() {
		if(!$(this).hasClass("flipped")) {
			$(this).children(".front").attr("src", unknown_path);
			$(this).removeClass("peak");
		}
	})

	$(".card").click(function() {
		toggle_card($(this));
	})
}

function restore_open() {
	$.each(open_cards, function(index, card) {
		card.obj.flip()
		board[card.id].visible = false
	})
	open_cards.length = 0
}

function toggle_card(card_holder) {
	if(open_cards.length >= 2) return;
	var board_pos = card_holder.data("card");
	var card = board[board_pos];
	if(!card.visible) {
		var co = {
			obj: card_holder,
			id: board_pos
		}
		open_cards.push(co)
		card.visible = true
		card_holder.flip()
	}
	if(open_cards.length == 2) {
		setTimeout(check_open, 900)
	}
}

function check_open() {
	var c1 = board[open_cards[0].id]
	var c2 = board[open_cards[1].id]
	if(c1.id == c2.id) {
		open_cards[0].obj.flash()
		open_cards[1].obj.flash()
		++players[current_player].score
		update_score()
		c1.found = true
		c2.found = true
		open_cards.length = 0
		--remaining_cards;
		if(remaining_cards <= 0) {
			setTimeout(win, 100)
		}
	} else {
		setTimeout(restore_and_next, 800)
	}
}

function restore_and_next() {
	restore_open()
	current_player = (current_player + 1) % 2;
	update_turn()
}

function win() {
	var winner = "It's a draw!";
	if(players[0].score > players[1].score) {
		winner = players[0].name + " wins!"
	} else if(players[1].score > players[0].score) {
		winner = players[1].name + " wins!"
	}
	$("#winner").html(winner)
	$("#win").fadeIn(1000);
}
