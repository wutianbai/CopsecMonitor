jQuery(function(){

    var $body = $('body');
    var $panel = $body.find('.panel');

    var mode= new Map(),modeName = new Map();
    var $modeNamSelect = $panel.find('select').first(),
        $modeSelect= $panel.find('select').last(),
        $listTable = $body.find('table').first(),
        $addTable = $body.find('table').last(),
        $addBtn = $addTable.find('td').last().find('button.btn-turquoise'),
        $updateBtn = $addTable.find('td').last().find('button.btn-info'),
        $cancleBtn = $addTable.find('td').last().find('button.btn-gray');

    var ethMap = new Map();
    var modeMap = new Map();
    $.when($.ajax(contextPath+"system/get")).done(function(data){

        modeMap.set('0',"0:load balancing(round-robin)")
        modeMap.set('1',"1:fault-tolerance(active-backup))")
        modeMap.set('2',"2:load balancing(xor))")
        modeMap.set('3',"3:fault-tolerance(broadcast))")
        modeMap.set('4',"4:IEEE 802.3ad Dynamic Link aggregation)")
        modeMap.set('5',"5:transmit load balancing)")
        modeMap.set('6',"6:adaptive load balancing)")
        if(data.data.INTERFACELIST){

            var arrays = new String(data.data.INTERFACELIST).split(",");
            for(var i=0;i< arrays.length;i++){

                var datas = new String(arrays[i]).split(":");
                ethMap.set(datas[0],datas[1]);
                addCheckBox(datas[0],datas[1]);
            }
        }
        if(data.data.BONDMODE){

            var arrays = $.parseJSON(data.data.BONDMODE);
            for(var i=0;i< arrays.length ;i++){

                addBondConfig(i,arrays[i]);
            }
        }

    });


    $addBtn.click(function(){

        var bondName = $modeNamSelect.find('option:selected').val(),
            bondMode = $modeSelect.find('option:selected').val(),port="";
        var $checkBoxs = $addTable.find("input[type=checkbox]:checked");
        if($checkBoxs.length <= 1 ){

            toastr.error("至少选择两个业务口进行绑定");
            return false;
        }
        $checkBoxs.each(function(e,i){


            port += $(this).val()+',';
        });
        port = new String(port).substring(0,port.length-1);
        var bond = {

            boundName:bondName,
            port:port,
            mode:bondMode
        }

        $.ajax({

            url:contextPath+'system/bond/add',
            data:{'boundName':bondName,'port':port,'mode':bondMode},
            dataType:'json',
            method:'POST',
            success:function(data){

                if(data.code == 200){

                    toastr.info(data.message);
                    addBondConfig($listTable.children().length +1,bond);
                }else{

                    toastr.error(data.message);
                }
            },
            error:function(jqxhr,status,error){

                toastr.error(error);
            }
        });

    });

    $updateBtn.click(function(){

        var bondName = $modeNamSelect.find('option:selected').val(),
            bondMode = $modeSelect.find('option:selected').val(),port='';
        var $checkBoxs = $addTable.find("input[type=checkbox]:checked");
        if($checkBoxs.length <= 1 ){

            toastr.error("至少选择两个业务口进行绑定");
            return false;
        }
        $checkBoxs.each(function(e,i){

            port += $(this).val()+',';
        });
        port = new String(port).substring(0,port.length-1);
        $.ajax({

            url:contextPath+'system/bond/update',
            data:{'boundName':bondName,'port':port,'mode':bondMode},
            dataType:'json',
            method:'POST',
            success:function(data){

                if(data.code == 200){

                    toastr.info(data.message);
                    $cancleBtn.trigger('click');
                    var index = $addTable.find("tr:nth-child(2)").attr('name');
                    var $tr = $listTable.find("tr[name="+index+"]");
                    var ports = new String(port).split(",");
                    var portName = "";
                    for(var i=0;i<ports.length;i++){

                        if(ports[i] != ','){

                            portName += ethMap.get(ports[i]) + ",";
                        }

                    }
                    $tr.find("td:nth-child(1)").html(bondName);
                    $tr.find("td:nth-child(2)").html(portName);
                    $tr.find("td:nth-child(3)").html(modeMap.get(mode));
                }else{

                    toastr.error(data.message);
                }
            },
            error:function(status){

                toastr.error(status);
            }
        });
    });


    $modeNamSelect.on('change',function(){

        var value = $modeNamSelect.find('option:selected').val();
        $modeNamSelect.children().each(function(i,e){

            if($(this).val() == value){

                $(this).attr('selected',true);
            }else{

                $(this).removeAttr('selected');
            }
        });
    });

    $modeSelect.on('change',function(){

        var value = $modeSelect.find('option:selected').val();
        $modeSelect.children().each(function(i,e){

            if($(this).val() == value){

                $(this).attr('selected',true);
            }else{

                $(this).removeAttr('selected');
            }
        });
    });

    //取消按钮
    $cancleBtn.click(function(){

        $addTable.find("input[type=text]").val("");
        swichBtn($addBtn);
        swichBtn($updateBtn);
        swichBtn($cancleBtn);
    })
    function addBondConfig(index,bond){
        var ports = new String(bond.port).split(",");
        var portName = "";
        for(var i=0;i<ports.length;i++){

            if(ports[i] != ','){

                portName += ethMap.get(ports[i]) + ",";
            }

        }
        var $tr =$("<tr name="+index+">" +
            "<td>"+bond.boundName+"</td>" +
            "<td>"+portName+"</td>" +
            "<td>"+modeMap.get(bond.mode)+"</td>" +
            "<td>" +
            "<button class='btn btn-info'>修改</button>" +
            "<button class='btn btn-red'>删除</button>" +
            "</td>" +
            "</tr>");
        $panel.find('table').first().append($tr);
        $tr.on('click','button:nth-child(1)',function(){

            if($addBtn.css('display') != 'none'){

                swichBtn($addBtn);
                swichBtn($updateBtn);
                swichBtn($cancleBtn);
            }
            setBond(bond.boundName,bond.mode,bond.port);
            $addTable.find("tr:nth-child(2)").attr('name',index);
        });

        $tr.on('click','button:nth-child(2)',function(){

            $.ajax({

                url:contextPath+'system//bond/delete',
                data:{'name':bond.boundName},
                method:'POST',
                dataType:'json',
                success:function (data) {

                    if (data.code == 200){

                        toastr.info(data.message);
                        $tr.remove();
                    } else{

                        toastr.error(data.message);
                    }
                }
            });

        });
    }

    function swichBtn(btn){

        if(btn.css('display') == 'none'){

            btn.css('display','block');
        }else{

            btn.css('display','none');
        }
    }

    function addCheckBox(k,v){

/*
        var $checkBox = $("<label><input type='checkbox' class='cbr-replaced' name='port' value="+k+">"+v+"</label>");
*/
        var $checkBox = $("<label class='checkbox-inline'><input type='checkbox' name='port' value="+k+">"+v+"</label>");
        $addTable.find('tr:nth-child(2) td').last().append($checkBox);
    }

    function setBond(name,mode,port){
        $modeNamSelect.find('option').each(function(e,i){

            if($(this).val() == name){

                $(this).attr('selected',true);
            }
        });
        $modeSelect.find('option').each(function(e,i){

            if($(this).val() == mode){

                $(this).attr('selected',true);
            }
        });

        $addTable.find("input[type=checkbox]").each(function(e,i){

            var ports = new String(port).split(",");
            for(var i=0;i<ports.length;i++){

                if($(this).val() == ports[i]){

                    $(this).attr('checked',true);
                }
            }
        })
    }

})
