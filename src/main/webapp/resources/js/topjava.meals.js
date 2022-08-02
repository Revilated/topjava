const mealAjaxUrl = "ajax/meals/";

// https://stackoverflow.com/a/5064235/548473
const ctx = {
    ajaxUrl: mealAjaxUrl
};

let filterForm;

// $(document).ready(function () {
$(function () {
    filterForm = $('#filterForm');
    makeEditable(
        $("#datatable").DataTable({
            "paging": false,
            "info": true,
            "columns": [
                {
                    "data": "dateTime"
                },
                {
                    "data": "description"
                },
                {
                    "data": "calories"
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
                    "desc"
                ]
            ]
        })
    );
});

function requestTableData() {
    if (hasFilter()) {
        applyFilter()
    } else {
        $.get(ctx.ajaxUrl, function (data) {
            updateTable(data)
        });
    }
}

function applyFilter() {
    $.ajax({
        type: "GET",
        url: ctx.ajaxUrl + "filter",
        data: filterForm.serialize()
    }).done(function (data) {
        updateTable(data);
    });
}

function resetFilter() {
    $("input[name='startDate']").val('')
    $("input[name='endDate']").val('')
    $("input[name='startTime']").val('')
    $("input[name='endTime']").val('')
    filterForm = undefined
    requestTableData()
}

function hasFilter() {
    return filterForm !== undefined
}
