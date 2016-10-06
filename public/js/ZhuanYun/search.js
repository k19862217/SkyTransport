String.prototype.trim = function () {
    return this.replace(/^\s+/g, "").replace(/\s+$/g, "");
}

function Search(obj, input) {
    var text = $("#txtSearchText").val();
    var url = PortalUrl + "Transport/LogisticsTransferTrace.aspx?code=" + $.trim(text);
    $(obj).attr("href", url);
    return true;
}
$(function () {

    function Placeholder() {
        function isPlaceAttr() {
            var input = document.createElement('input');
            return 'placeholder' in input;
        }
        if (!isPlaceAttr()) {
            var logisticInput = $("#txtSearchText");
            if (logisticInput.val() == "" && logisticInput.attr("placeholder") != "") {
                logisticInput.val(logisticInput.attr("placeholder"));
                logisticInput.focus(function () {
                    if (logisticInput.val() == logisticInput.attr("placeholder")) logisticInput.val("");
                });
                logisticInput.blur(function () {
                    if (logisticInput.val() == "") logisticInput.val(logisticInput.attr("placeholder"));
                });
            }
            var logisticInput1 = $("#txtSearchText1");
            if (logisticInput1.val() == "" && logisticInput1.attr("placeholder") != "") {
                logisticInput1.val(logisticInput1.attr("placeholder"));
                logisticInput1.focus(function () {
                    if (logisticInput1.val() == logisticInput1.attr("placeholder")) logisticInput1.val("");
                });
                logisticInput1.blur(function () {
                    if (logisticInput1.val() == "") logisticInput1.val(logisticInput1.attr("placeholder"));
                });
            }
        }
    };

    Placeholder();
    $("#btnsearch").bind("click", function () {
        if ($("#txtSearchText1").val() == "请输入商家物流号" || $("#txtSearchText1").val() == "") {
            $("#txtSearchText1").focus();
            return;
        }
        window.location.href = 'Transport/LogisticsTransferTrace.aspx?code=' + $.trim($("#txtSearchText1").val());
    });
});
