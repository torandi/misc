var sort_direction=1
var cur_sort=null

$(function() {
	$(".min option").attr("selected", false);
	$(".min option:first").attr("selected", true);

	$(".max option").attr("selected", false);
	$(".max option:last").attr("selected", true);

	$("#search").submit(function() {
		$.ajax({
			url: "search.php", 
			data: $(this).serialize(),
			success: function(data) {
				$("#results").html(data);
				$(".sortable thead tr th").removeClass("cur_sort")
				$(".sortable thead tr th").removeClass("sort_asc")
				$(".sortable thead tr th").removeClass("sort_desc")

				cur_sort='pris';
				sort_direction = 1;

				$(".sortable thead tr th." + cur_sort).addClass("cur_sort")
				$(".sortable thead tr th." + cur_sort).addClass("sort_asc")
			}
		})
		return false;
	})

  $(".sortable thead tr th").click(function() {
		$(".sortable thead tr th").removeClass("cur_sort")
		$(".sortable thead tr th").removeClass("sort_asc")
		$(".sortable thead tr th").removeClass("sort_desc")

    cur_class = $(this).attr('class')
    if(cur_sort == cur_class) {
      sort_direction = sort_direction*-1
    } else {
      sort_direction = 1
      cur_sort = cur_class
    }
    listitems = $(".sortable tbody tr").sort(function(a,b) {
      av = sort_data(a, cur_class)
      bv = sort_data(b, cur_class)
      return (av < bv ? -1 : (av > bv ? 1 : 0))*sort_direction
    })

    $.each(listitems, function(idx, itm) {
      $(".sortable tbody").append(itm)
    })

    //Decoration
    $(this).addClass("cur_sort")
    if(sort_direction == 1) {
      $(this).addClass("sort_asc")
    } else {
      $(this).addClass("sort_desc")
    }
  })
})

function sort_data(itm, cls) {
  sort_item = $(itm).children("."+cls)
  if(sort_item.data("numerical_value") == undefined) {
    return sort_item.text()
  } else {
    return sort_item.data("numerical_value")
  }
}

