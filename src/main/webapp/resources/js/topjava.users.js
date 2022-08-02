const userAjaxUrl = "admin/users/";

// https://stackoverflow.com/a/5064235/548473
const ctx = {
    ajaxUrl: userAjaxUrl
};

// $(document).ready(function () {
$(function () {
    makeEditable(
        $("#datatable").DataTable({
            "paging": false,
            "info": true,
            "columns": [
                {
                    "data": "name"
                },
                {
                    "data": "email"
                },
                {
                    "data": "roles"
                },
                {
                    "data": "enabled"
                },
                {
                    "data": "registered"
                },
                {
                    "defaultContent": "Edit",
                    "orderable": false
                },
                {
                    "defaultContent": "Delete",
                    "orderable": false
                }
            ],
            "order": [
                [
                    0,
                    "asc"
                ]
            ]
        })
    );
});

function enableUser(checkBox) {
    let tr = $(checkBox).closest("tr");
    let id = tr.attr("id");
    $.ajax({
        type: "PATCH",
        url: ctx.ajaxUrl + id + "/enable?isEnabled=" + checkBox.checked,
    }).done(function () {
        tr.attr("data-user-enabled", checkBox.checked);
        successNoty(checkBox.checked ? "Enabled" : "Disabled");
    }).fail(function () {
        requestTableData();
    });
}