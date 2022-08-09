const mealAjaxUrl = "profile/meals/";

// https://stackoverflow.com/a/5064235/548473
const ctx = {
    ajaxUrl: mealAjaxUrl,
    updateTable: function () {
        $.ajax({
            type: "GET",
            url: mealAjaxUrl + "filter",
            data: $("#filter").serialize()
        }).done(updateTableByData);
    },
    ajaxGetDataType: "ui_datetime_json"
}

function createRangeDatePickers(startId, endId) {
    let startDatePicker = $("#" + startId)
    let endDatePicker = $("#" + endId)
    startDatePicker.datetimepicker({
        timepicker: false,
        format: "Y-m-d",
        onShow: function (ct) {
            this.setOptions({
                maxDate: endDatePicker.val() ? endDatePicker.val() : false
            })
        }
    })
    endDatePicker.datetimepicker({
        timepicker: false,
        format: "Y-m-d",
        onShow: function (ct) {
            this.setOptions({
                minDate: startDatePicker.val() ? startDatePicker.val() : false
            })
        }
    })
}

function createRangeTimePickers(startId, endId) {
    let startTimePicker = $("#" + startId)
    let endTimePicker = $("#" + endId)
    startTimePicker.datetimepicker({
        datepicker: false,
        format: "H:i",
        onShow: function (ct){
            this.setOptions({
                maxTime: endTimePicker.val() ? endTimePicker.val() : false
            })
        }
    })
    endTimePicker.datetimepicker({
        datepicker: false,
        format: "H:i",
        onShow: function (ct){
            this.setOptions({
                minTime: startTimePicker.val() ? startTimePicker.val() : false
            })
        }
    })
}

function createDateTimePicker(id) {
    $("#" + id).datetimepicker({
        format: "Y-m-d H:i"
    });
}

function clearFilter() {
    $("#filter")[0].reset();
    $.get(mealAjaxUrl, updateTableByData);
}

function formatDateTime(dateTime) {
    return dateTime.substring(0, 16).replace("T", " ")
}

$(function () {
    $.ajaxSetup({
        contents: {
            ui_datetime_json: "ui_datetime_json"
        },
        converters: {
            "json ui_datetime_json": function (data) {
                if (data.hasOwnProperty("dateTime")) {
                    let newData = Object.assign({}, data);
                    newData.dateTime = formatDateTime(data.dateTime)
                    return newData;
                } else {
                    return data;
                }
            }
        }
    });
    createRangeDatePickers("startDate", "endDate")
    createRangeTimePickers("startTime", "endTime")
    createDateTimePicker("dateTime")
    makeEditable(
        $("#datatable").DataTable({
            "ajax": {
                "url": mealAjaxUrl,
                "dataSrc": ""
            },
            "paging": false,
            "info": true,
            "columns": [
                {
                    "data": "dateTime",
                    "render": function (dateTime, type, row) {
                        if (type === "display") {
                            return formatDateTime(dateTime)
                        }
                        return dateTime;
                    }
                },
                {
                    "data": "description"
                },
                {
                    "data": "calories"
                },
                {
                    "orderable": false,
                    "defaultContent": "",
                    "render": renderEditBtn
                },
                {
                    "orderable": false,
                    "defaultContent": "",
                    "render": renderDeleteBtn
                }
            ],
            "order": [
                [
                    0,
                    "desc"
                ]
            ],
            "createdRow": function (row, data, dataIndex) {
                $(row).attr("data-meal-excess", data.excess);
            }
        })
    );
});