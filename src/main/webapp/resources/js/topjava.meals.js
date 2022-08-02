const mealAjaxUrl = "ajax/meals/";

// https://stackoverflow.com/a/5064235/548473
const ctx = {
    ajaxUrl: mealAjaxUrl
};

let filterForm;

// $(document).ready(function () {
$(function () {
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

requestTableData = (function (_super) {
    return function () {
        if (hasFilter()) {
            requestFilteredTabledData();
        } else {
            _super.apply(this)
        }
    }
})(requestTableData)

function requestFilteredTabledData() {
    filterForm = $('#filterForm');
    $.ajax({
        type: "GET",
        url: ctx.ajaxUrl + "filter",
        data: filterForm.serialize()
    }).done(function (data) {
        updateTable(data);
    });
}

function resetFilter() {
    filterForm[0].reset()
    filterForm = undefined;
    requestTableData();
}

function hasFilter() {
    return filterForm !== undefined;
}
