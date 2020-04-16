jQuery(function () {
    let $body = $('body');
    /**
     * 保存按钮
     */
    $('#save').on('click', function () {
        let orig = $body.find('.form-group:nth-child(1) input').val(),
            newPwd = $body.find('.form-group:nth-child(2) input').val(),
            confirm = $body.find('.form-group:nth-child(3) input').val();
        let test = /^(?=.*[0-9].*)(?=.*[A-Z].*)(?=.*[a-z].*).{8,32}$/;

        if (orig === "") {
            toastr.error("请输入原密码");
            return false;
        }

        if (newPwd === "") {
            toastr.error("请输入新密码");
            return false;
        }

        if (!newPwd.trim().match(test)) {
            toastr.error("密码格式不正确，请重新输入");
            return false;
        }

        if (confirm !== newPwd) {
            toastr.error("密码不一致，请重新输入");
            return false;
        }

        $.ajax({
            url: contextPath + 'system/code/update',
            data: {'password': orig, 'newCode': confirm},
            method: 'POST',
            dataType: 'json',
            success: function (data) {
                if (data.code === 200) {
                    toastr.info(data.message);
                    setTimeout("$(location).attr('href', '/')",1000);
                } else {
                    toastr.error(data.message);
                }
            }
        });
    });

    /**
     * 重置按钮
     */
    $('#empty').on('click', function () {
        $("#message").modal('hide');
        $('body').find("input").val("");
    });
});