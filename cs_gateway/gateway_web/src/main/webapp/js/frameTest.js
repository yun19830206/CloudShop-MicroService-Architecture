//jQuery初始化页面
$(document).ready(function(){

    //第一项：展示服务注册的结果
    initServiceGovern();

    //第二项：监控3台服务器的资源占用情况
    monitorWebResources();

    //第三项：轮训查看所有ServiceCall调用监控结果(GateWay为发起方)
    monitorServiceCallResult();

    //第四项：调试服务调用返回类型：正常，失败，超时，异常接口(注意看ServiceCall调用监控结果)
    debugServiceCall();

});


//第一项：展示服务注册的结果
function initServiceGovern() {
    //1-1:查看服务注册结果--实时Zookeeper方式(user.get)
    $("#buttonServiceDiscoverCached").click( function () {
        $.ajax({
            type:	"post",
            url:	"/api/csmonitor/service/getOneServiceList?serviceName=order.get",
            timeout:	300000,
            data:	 null,
            async:	true,
            dataType:	"json", //还可以是text,xml,实际返回值一定要和dataType统一，不然前台是报parsererror，并作为调用失败处理。
            success:	//请求返回成功调用的函数,参数data是返回的值(此时已经是object)，textStatus状态
                function(data,textStatus){
                    //alert("调用成功后的JSON.stringify(data)="+JSON.stringify(data.data));
                    //var result = new JSONFormat(data,4).toString();
                    var result = JSON.stringify(data.data,null,4) ;
                    //<pre></pre> 用于格式化输出
                    $("#tdServiceDiscoverCached").html('<pre>'+result+'</pre>');
                },
            error:	//请求失败时调用的函数,
                function(XMLHttpRequest,textStatus,errorThrowm){
                    alert("查询调用失败，请联系管理员。错误描述为:"+errorThrowm);
                    return ;
                }
        });
    });

    //1-2:查看服务注册结果--实时Zookeeper方式(user.get)
    $("#buttonServiceDiscoverZookeeper").click( function () {
        $.ajax({
            type:	"post",
            url:	"/api/csmonitor/service/getAllSimpleServiceListFromZookeeper",
            timeout:	300000,
            data:	 null,
            async:	true,
            dataType:	"json", //还可以是text,xml,实际返回值一定要和dataType统一，不然前台是报parsererror，并作为调用失败处理。
            success:	//请求返回成功调用的函数,参数data是返回的值(此时已经是object)，textStatus状态
                function(data,textStatus){
                    //alert("调用成功后的JSON.stringify(data)="+JSON.stringify(data.data));
                    //var result = new JSONFormat(data,4).toString();
                    var result = JSON.stringify(data.data,null,4) ;
                    //<pre></pre> 用于格式化输出
                    $("#tdServiceDiscoverZookeeper").html('<pre>'+result+'</pre>');
                },
            error:	//请求失败时调用的函数,
                function(XMLHttpRequest,textStatus,errorThrowm){
                    alert("查询调用失败，请联系管理员。错误描述为:"+errorThrowm);
                    return ;
                }
        });
    });
}

//第二项：监控3台服务器的资源占用情况
function monitorWebResources(){
    //2-1: 监控Gateway服务资源
    $("#buttonGatewayRes").click( function () {
        setInterval(function(){
            $.ajax({
                type:	"get",
                url:	"/api/csmonitor/service/getWebResource?serviceName=CSGateway",
                timeout:	300000,
                data:	 null,
                async:	true,
                dataType:	"json", //还可以是text,xml,实际返回值一定要和dataType统一，不然前台是报parsererror，并作为调用失败处理。
                success:	//请求返回成功调用的函数,参数data是返回的值(此时已经是object)，textStatus状态
                    function(data,textStatus){
                        //alert("调用成功后的JSON.stringify(data)="+JSON.stringify(data.data));
                        var result = JSON.stringify(data.data,null,4) ;
                        //<pre></pre> 用于格式化输出
                        $("#tdGatewayRes").html('<pre>'+result+'</pre>');
                    },
                error:	//请求失败时调用的函数,
                    function(XMLHttpRequest,textStatus,errorThrowm){
                        alert("查询调用失败，请联系管理员。错误描述为:"+errorThrowm);
                        return ;
                    }
            });
        },5000);
    });

    //2-2: 监控order.resMonitorA服务资源
    $("#buttonOrderA").click( function () {
        setInterval(function(){
            $.ajax({
                type:	"get",
                url:	"/api/csmonitor/service/getWebResource?serviceName=order.resMonitorA",
                timeout:	300000,
                data:	 null,
                async:	true,
                dataType:	"json", //还可以是text,xml,实际返回值一定要和dataType统一，不然前台是报parsererror，并作为调用失败处理。
                success:	//请求返回成功调用的函数,参数data是返回的值(此时已经是object)，textStatus状态
                    function(data,textStatus){
                        //alert("调用成功后的JSON.stringify(data)="+JSON.stringify(data.data));
                        //var result = new JSONFormat(data,4).toString();
                        var result = JSON.stringify(data.data,null,4) ;
                        //<pre></pre> 用于格式化输出
                        $("#tdOrderA").html('<pre>'+result+'</pre>');
                    },
                error:	//请求失败时调用的函数,
                    function(XMLHttpRequest,textStatus,errorThrowm){
                        alert("查询调用失败，请联系管理员。错误描述为:"+errorThrowm);
                        return ;
                    }
            });
        },5000);
    });

    //2-3: 监控order.resMonitorB服务资源
    $("#buttonOrderB").click( function () {
        setInterval(function(){
            $.ajax({
                type:	"get",
                url:	"/api/csmonitor/service/getWebResource?serviceName=order.resMonitorB",
                timeout:	300000,
                data:	 null,
                async:	true,
                dataType:	"json", //还可以是text,xml,实际返回值一定要和dataType统一，不然前台是报parsererror，并作为调用失败处理。
                success:	//请求返回成功调用的函数,参数data是返回的值(此时已经是object)，textStatus状态
                    function(data,textStatus){
                        //alert("调用成功后的JSON.stringify(data)="+JSON.stringify(data.data));
                        //var result = new JSONFormat(data,4).toString();
                        var result = JSON.stringify(data.data,null,4) ;
                        //<pre></pre> 用于格式化输出
                        $("#tdOrderB").html('<pre>'+result+'</pre>');
                    },
                error:	//请求失败时调用的函数,
                    function(XMLHttpRequest,textStatus,errorThrowm){
                        alert("查询调用失败，请联系管理员。错误描述为:"+errorThrowm);
                        return ;
                    }
            });
        },5000);
    });
}

//第三项：轮训查看所有ServiceCall调用监控结果(GateWay为发起方)
function monitorServiceCallResult(){
    $("#buttonServiceCallResultButton").click( function () {
        setInterval(function(){
            $.ajax({
                type:	"get",
                url:	"/api/csmonitor/service/getServiceCallMonitorResult",
                timeout:	300000,
                data:	 null,
                async:	true,
                dataType:	"json", //还可以是text,xml,实际返回值一定要和dataType统一，不然前台是报parsererror，并作为调用失败处理。
                success:	//请求返回成功调用的函数,参数data是返回的值(此时已经是object)，textStatus状态
                    function(data,textStatus){
                        //alert("调用成功后的JSON.stringify(data)="+JSON.stringify(data.data));
                        var result = JSON.stringify(data.data,null,4) ;
                        //<pre></pre> 用于格式化输出
                        $("#tdServiceCallResultButton").html('<pre>'+result+'</pre>');
                    },
                error:	//请求失败时调用的函数,
                    function(XMLHttpRequest,textStatus,errorThrowm){
                        alert("查询调用失败，请联系管理员。错误描述为:"+errorThrowm);
                        return ;
                    }
            });
        },5000);
    });
}

//第四项：调试服务调用返回类型：正常，失败，超时，异常接口(注意看ServiceCall调用监控结果)
function debugServiceCall(){

    $("#bDebugRestNormal").click( function () {
        var callType = "rest", interfaceType = "normal" ;
        var url = "/api/gateway/debugServiceCall?callType="+callType+"&interfaceType="+interfaceType ;
        postAjax(url,null);
    });
    $("#bDebugRestFail").click( function () {
        var callType = "rest", interfaceType = "fail" ;
        var url = "/api/gateway/debugServiceCall?callType="+callType+"&interfaceType="+interfaceType ;
        postAjax(url,null);
    });
    $("#bDebugRestTimeout").click( function () {
        var callType = "rest", interfaceType = "timeout" ;
        var url = "/api/gateway/debugServiceCall?callType="+callType+"&interfaceType="+interfaceType ;
        postAjax(url,null);
    });
    $("#bDebugRestException").click( function () {
        var callType = "rest", interfaceType = "exception" ;
        var url = "/api/gateway/debugServiceCall?callType="+callType+"&interfaceType="+interfaceType ;
        postAjax(url,null);
    });

    $("#bDebugHystrixNormal").click( function () {
        var callType = "hystrix", interfaceType = "normal" ;
        var url = "/api/gateway/debugServiceCall?callType="+callType+"&interfaceType="+interfaceType ;
        postAjax(url,null);
    });
    $("#bDebugHystrixFail").click( function () {
        var callType = "hystrix", interfaceType = "fail" ;
        var url = "/api/gateway/debugServiceCall?callType="+callType+"&interfaceType="+interfaceType ;
        postAjax(url,null);
    });
    $("#bDebugHystrixTimeout").click( function () {
        var callType = "hystrix", interfaceType = "timeout" ;
        var url = "/api/gateway/debugServiceCall?callType="+callType+"&interfaceType="+interfaceType ;
        postAjax(url,null);
    });
    $("#bDebugHystrixException").click( function () {
        var callType = "hystrix", interfaceType = "exception" ;
        var url = "/api/gateway/debugServiceCall?callType="+callType+"&interfaceType="+interfaceType ;
        postAjax(url,null);
    });
    $("#bDebugHystrixProduct").click( function () {
        var callType = "hystrix", interfaceType = "product" ;
        var url = "/api/gateway/debugServiceCall?callType="+callType+"&interfaceType="+interfaceType ;
        postAjax(url,null);
    });

}



/** 简单通用性PostAjax请求 */
function postAjax(url, psotParam) {
    $.ajax({
        type:	"post",
        url:	url,
        timeout:	300000,
        data:	 psotParam,
        async:	true,
        dataType:	"json", //还可以是text,xml,实际返回值一定要和dataType统一，不然前台是报parsererror，并作为调用失败处理。
        success:	//请求返回成功调用的函数,参数data是返回的值(此时已经是object)，textStatus状态
            function(data,textStatus){
                return data.data ;
            },
        error:	//请求失败时调用的函数,
            function(XMLHttpRequest,textStatus,errorThrowm){
                alert("查询调用失败，请联系管理员。错误描述为:"+errorThrowm);
                return "查询调用失败，请联系管理员。错误描述为:"+errorThrowm;
            }
    });
}


