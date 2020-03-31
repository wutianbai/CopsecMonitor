jQuery(function () {
    // var $ = jQuery;
    // var navigateUrl = '../navigate';
    // $(".navbar").load(navigateUrl + ' .navbar-inner');
    //
    // $('body').on('mouseover', 'li.has-sub', function () {
    //     $(this).addClass('hover');
    // });
    //
    // $('body').on('mouseout', 'li.has-sub', function () {
    //     $(this).removeClass('hover');
    // });
    // $("body").find(".panel-heading").css("cursor", "pointer");
    //
    // $('body').on('click', '.panel-heading', function () {
    //     var device = $(this).children().first();
    //     if (device.hasClass('vertical-top')) {
    //         return false;
    //     }
    //     if ($(this).parent().hasClass('collapsed')) {
    //         $(this).parent().removeClass('collapsed');
    //     } else {
    //         $(this).parent().addClass('collapsed');
    //     }
    // });
    //
    // $("body").on('click', ".collapse-icon", function () {
    //     if ($(this).parent().parent().parent().parent().hasClass('collapsed')) {
    //         $(this).parent().parent().parent().parent().removeClass('collapsed');
    //     } else {
    //         $(this).parent().parent().parent().parent().addClass('collapsed');
    //     }
    // });

    // $("body").on('click', ".expand-icon", function () {
    //     if ($(this).parent().parent().parent().parent().hasClass('collapsed')) {
    //         $(this).parent().parent().parent().parent().removeClass('collapsed');
    //     } else {
    //         $(this).parent().parent().parent().parent().addClass('collapsed');
    //     }
    // });

    getWarningStatus();
    setInterval(getWarningStatus, 2000);
});

function getWarningStatus() {
    $.ajax({
        url: contextPath + "warning/event/search/",
        data: {
            "iDisplayStart": 0,
            "iDisplayLength": 10,
        },
        method: 'POST',
        dataType: "json",
        success: function (data) {
            if (data.iTotalDisplayRecords > 0) {
                $("#badge").removeClass("badge-green");
                $("#badge").addClass("badge-red");
                $("#badge").text(data.iTotalDisplayRecords);
                $("#warningStrong").text(data.iTotalDisplayRecords);
            } else {
                $("#badge").removeClass("badge-red");
                $("#badge").addClass("badge-green");
                $("#badge").text("");
                $("#warningStrong").text("0");
            }
            setScrollbar(data.data);
        },
        error: function (jxq, status, error) {
            toastr.error("请求错误或超时");
        }
    });
}

function setScrollbar(data) {
    let s = "";
    $.each(data, function (index, value) {
        let str;
        switch (value.eventType) {
            case "非告警":
                str = '<li class="active notification-info">';
                break;
            case "一般告警":
                str = '<li class="active notification-warning">';
                break;
            case "严重告警":
                str = '<li class="active notification-danger">';
                break;
            default:
                str = '<li class="active notification-success">';
        }
        str += '<a href="' + contextPath + 'system/warningEvent">';
        switch (value.eventSource) {
            case "中央处理机":
                str += '<i class="fa fa-sitemap"></i>';
                break;
            // case "磁盘":
            //     str += '<li class="fa fa-floppy-o"></i>';
            //     break;
            // case "内存":
            //     str += '<li class="fa fa-pie-chart"></i>';
            //     break;
            case "用户":
                str += '<i class="fa fa-user"></i>';
                break;
            // case "系统类型":
            //     str += '<i class="fa fa-cog"></i>';
            //     break;
            // case "系统版本":
            //     str += '<i class="fa fa-credit-card"></i>';
            //     break;
            // case "系统补丁":
            //     str += '<i class="fa fa-folder-open"></i>';
            //     break;
            default:
                str += '<i class="fa fa-share-alt"></i>';
        }
        str += '<span class="line"><strong>' + value.eventDetail + '</strong></span>';
        str += '<span class="line small">设备ID[' + value.deviceId + ']</span>';
        str += '<span class="line small time">' + value.eventTime + '</span></a></li>';
        s += str;
    });
    $("#scrollbar").html(s);
}

function handleAllWarningEvent() {
    $.ajax({
        url: contextPath + "warning/event/handleAll/",
        data: {},
        method: 'POST',
        dataType: "json",
        success: function (data) {
            if (data.code === 200) {
                toastr.info(data.message);
            } else {
                toastr.error(data.message);
            }
        },
        error: function (jxq, status, error) {
            toastr.error("请求错误或超时");
        }
    });
    getWarningStatus();
}

/**
 * _url:传入链接地址
 * _method:post or get
 * _idCheckBox = tableId
 */
function copsec_search() {
    this.url = ''; //查询url
    this.method = ''; //接口方法类型，post，get
    this.data = null; //查询使用数据参数
    this.idCheckBox = '';//绑定的checkboxid
    this.list = ''; //tbody id
    this.pageInfo = ''; //分页信息id
    this.page = '';//分页id
    this.searchId = '';//搜索按钮id

    /**
     * 创建search对象
     */
    this.create = function (separator) {
        if (arguments.length != 7) {
            return false;
        }
        this.url = arguments[0];
        this.method = arguments[1];
        this.idCheckBox = arguments[2];
        this.list = arguments[3];
        this.pageInfo = arguments[4];
        this.page = arguments[5];
        this.searchId = arguments[6];
    };

    /**
     * 设置查询参数
     */
    this.put = function (_data) {
        this.data = _data;
    };

    /**
     * 执行搜索函数方法
     */
    this.getData = function (callback) {
        var $pageInfo = $('#' + this.pageInfo);

        var $page = $('#' + this.page);

        var $state = $("#" + this.idCheckBox + " thead tr th input[type=checkbox]");
        if ($state.is(':checked')) {
            $state.prop('checked', false).trigger('change');
        }
        $.ajax({
            url: this.url,
            method: this.method,
            dataType: "json",
            data: this.data,
            success: function (data, status, jqXHR) {
                $pageInfo.children().remove();
                var $tip = $("<label>第</label>");
                var $select = $("<select class='input-sm'></select>"),
                    $sizeSelect = $("<select class='input-sm'><option value='10'>10</option><option value='20'>20</option><option value='50'>50</option><option value='100'>100</option></select>");

                var selectStart = 1, selectEnd = 0;
                if (data.totalPages <= 10) {
                    selectEnd = data.totalPages;
                } else if (data.totalPages >= 10) {
                    if (data.number - 10 > 0) {
                        selectStart = data.number - 10;
                    } else {
                        selectStart = 1;
                    }

                    if (data.number + 10 <= data.totalPages) {
                        selectEnd = data.number + 10;
                    } else {
                        selectEnd = data.totalPages;
                    }
                }
                for (var num = selectStart; num <= selectEnd; num++) {
                    if (num === (data.number + 1)) {
                        $select.append("<option value=" + num + " selected>" + num + "</option>");
                    } else {
                        $select.append("<option value=" + num + ">" + num + "</option>");
                    }
                }
                $tip.append($select);
                $tip.append("页，共" + data.totalElements + "条记录，共" + data.totalPages + "页，每页显示");
                $tip.append($sizeSelect);
                $tip.append("条");
                $pageInfo.append($tip);

                $page.children().remove();
                var start = 0, total = 0;
                if (data.totalPages <= 5) {
                    start = 1;
                    total = data.totalPages;
                } else if (data.totalPages > 5) {
                    var _c = parseInt(data.number + 1);
                    if ((_c - 2) <= 0 && (_c + 2) <= 5) {
                        start = 1;
                        total = 5;
                    } else if ((_c + 2) > data.totalPages) {
                        total = data.totalPages;
                    } else {
                        total = (_c + 2);
                    }
                    if ((_c - 2) >= 1) {
                        start = (_c - 2);
                    } else {
                        start = 1;
                    }
                    if (_c === data.totalPages) {
                        start = _c - 4;
                        end = data.totalPages;
                    }
                }

                if (data.number === 0) {
                    $page.append("<li class='paginate_button previous disabled'><a href='#'>前一页</a></li>");
                } else {
                    $page.append("<li class='paginate_button previous'><a>前一页</a href='#'></li>");
                }

                for (; start <= total; start++) {
                    if (start === (data.number + 1)) {
                        $page.append("<li class='paginate_button active'><a href='#'>" + start + "</a></li>");
                    } else {
                        $page.append("<li class='paginate_button'><a href='#'>" + start + "</a></li>");
                    }
                }

                if (data.number + 1 === data.totalPages) {
                    $page.append("<li class='paginate_button next disabled'><a href='#'>后一页</a>");
                } else {
                    $page.append("<li class='paginate_button next'><a href='#'>后一页</a>");
                }
                callback(data);
                $("#pageInfo").find("select:nth-child(2) option[value=" + data.size + "]").attr('selected', true);
            }
        });
    };

    /**
     * 执行chechbox绑定
     */
    this.bind = function () {
        var $state = $("#" + this.idCheckBox + " thead input[type='checkbox']");
        var _list = this.list;
        $state.on('change', function (ev) {
            var $chcks = $("#" + _list + " tr td input[type='checkbox']");
            if ($state.is(':checked')) {
                $chcks.prop('checked', true).trigger('change');
            } else {
                $chcks.prop('checked', false).trigger('change');
            }
        });
    };

    /**
     * 获取checked的checkbox
     */
    this.getCheckBox = function () {
        return $("#" + this.list + " tr td input[type='checkbox']:checked");
    };

    /**
     * 执行分页绑定
     */
    this.bindPageLink = function (callback, fillData) {
        var copsec = Object.create(this);
        $("#" + this.page).on('click', 'li a', function () {

            var result = 0;
            if ($(this).parent().hasClass('previous')) {
                result = parseInt($("#" + copsec.page + " li[class~='active']").children().first().text()) - 1;
                if (!$.isNumeric(result) || result <= 0) {
                    return false;
                }
            } else if ($(this).parent().hasClass('next')) {
                result = parseInt($("#" + copsec.page + " li[class~='active']").children().first().text()) + 1;
            } else {
                result = ($(this).text());
            }
            var data = callback(result);
            copsec.put(data);
            copsec.getData(fillData);
        });
    };

    /**
     * 执行查询绑定
     */
    this.searchBind = function (dataCall, fillCallback) {
        var $proxy = Object.create(this);
        $("#" + this.searchId).on('click', function () {

            var _condition = dataCall(1);
            $proxy.put(_condition);
            $proxy.getData(fillCallback);
        });
        this.selectBind(dataCall, fillCallback);
    };

    this.search = function (data, callback) {
        this.put(data);
        this.getData(callback);
    };

    this.selectBind = function (dataCall, callback) {
        var $proxy = Object.create(this);
        var $pageInfo = $('#' + this.pageInfo);
        $pageInfo.on('change', 'select:nth-child(1)', function () {

            var $select = $(this);
            $proxy.search(dataCall($select.find('option:selected').val()), callback);
        });
        $pageInfo.on('change', 'select:nth-child(2)', function () {
            $proxy.search(dataCall(1), callback);
        });
    };
}

//复选框全选功能
function checkAll(id) {
    var $chcks = $("#" + id + " tbody input[type='checkbox']");
    $chcks.prop('checked', true).trigger('change');
}

//复选框不选功能
function uncheckAll(id) {
    var $chcks = $("#" + id + " tbody input[type='checkbox']");
    $chcks.prop('checked', false).trigger('change');
}

//清空输入框
function clearCondition(id) {
    $("#" + id).find('input').removeAttr('disabled').val('');
}

function addButton(modalId, buttonTitle, button) {
    $("#" + modalId).find(".modal-title").text(buttonTitle);
    if ($("#" + modalId).find(".modal-footer button").length == 2) {
        $("#" + modalId).find(".modal-footer button:nth-child(1)").remove();
    }
    $("#" + modalId).find(".modal-footer button").before(button);
    $("#" + modalId).modal('show', {backdrop: 'static'});
}

function isEmpty(obj) {
    return obj === "" || !obj;
}

function isNumber(num) {
    return $.isNumeric(num);
}

function isId(str) {
    var reg = /[a-zA-Z0-9]+/;
    return reg.test(str);
}

function ipTest(ip) {
    var regStr = /^((25[0-5]|2[0-4]\d|[01]?\d\d?)($|(?!\.$)\.)){4}$/;
    if (!ip) {
        return false;
    } else if ($.trim(ip) === "0.0.0.0") {
        return false;
    } else {
        return ip.match(regStr);
    }
}

function formatSize2(size, pointLength, units) {
    if (size === 0) {
        return "0B";
    }
    var unit = units || ['B', 'K', 'M', 'G', 'TB'];

    while ((unit = units.shift()) && size > 1024) {
        size = size / 1024;
    }
    return (unit === 'B' ? Number(size).toFixed(pointLength || 2) : Number(size).toFixed(pointLength || 2)) +
        unit;
}

function isLinuxPath(path) {
    var reg = /^\/(\w+\/?)+$/;
    return reg.test(path);
}

function reloadSelect(id, placeholder) {
    $select = $("#" + id);
    $select.select2({
        placeholder: placeholder,
        allowClear: true
    }).on('select2-open', function () {
        // Adding Custom Scrollbar
        $(this).data('select2').results.addClass('overflow-hidden').perfectScrollbar();
    });
}

function getLength(obj) {
    return Object.keys(obj).length;
}