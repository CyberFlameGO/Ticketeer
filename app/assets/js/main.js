$("#ticket-table").find("tbody").click(function(e){
    e = $(e.target);
    if (!e.hasClass("btn")) {
        var target = e.closest("tr");
        window.location = target.data('url');
    }
});
$(".ajax-form-refresh").submit(function(e) {
    e.preventDefault();
    var data = $(e.target).serialize();
    $.ajax({
        url: e.target.action,
        type: e.target.method,
        data: data
    }).done(function(data) {
        location.reload();
    });
});
$(".remove-btn").click(function(e) {
    if (confirm("Are you sure?")) {
        var row = $(e.target).closest(".removable");
        $.post(row.data('removeurl'), function(){
        }).done(function() {
            row.slideUp(function(){
                row.remove();
            });
        });
    }
});
function editTextSubmit(e) {
    e = $(e);
    var content = e.closest('.editable').find('.editing');
    $.post(e.closest(".editable").data('editurl'), {"content": content.val()}, function(data) {
    }).done(function() {
        content.parent().data('editing', 'n');
        content.parent().html(content.val());
    });
}
$(".response-edit-btn").click(function(e){
    var ed = $(e.target).closest(".editable").find(".ticket-content");
    if (ed.data('editing') != 'y') {
        ed.data('editing', 'y');
        var h = ed.text().trim();
        ed.html("<textarea class='form-control editing' /><p><button class='btn btn-success' onclick='editTextSubmit(this)'>Finish editing</button></p>");
        ed.children(".editing").val(h);
    }
});