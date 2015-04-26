/*
 *  Copyright (c) 2005-2014, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 *
 *  WSO2 Inc. licenses this file to you under the Apache License,
 *  Version 2.0 (the "License"); you may not use this file except
 *  in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing,
 *  software distributed under the License is distributed on an
 *  "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 *  KIND, either express or implied.  See the License for the
 *  specific language governing permissions and limitations
 *  under the License.
 *
 */
$(function() {
    $(document).ready(function() {

        $(".implementation_methods").change(function(event){
        $(".implementation_method").hide();
        $(".implementation_method_"+$(this).val()).show();
    });


    $('#endpointType').on('change',function(){
        var endpointType = $('#endpointType').find(":selected").val();
        if(endpointType == "secured"){
            $('#credentials').show();
        }
        else{
            $('#credentials').hide();
        }
    });
    $('#endpointType').trigger('change');

    /*$("#implement_form").submit(function (e) {
      e.preventDefault();
    });*/

    /*var v = $("#implement_form").validate({
        submitHandler: function(form) {        
        var designer = APIDesigner();
        APP.update_ep_config();
        $('#swagger').val(JSON.stringify(designer.api_doc));
        $('#saveMessage').show();
        $('#saveButtons').hide();
        $(form).ajaxSubmit({
            success:function(responseText, statusText, xhr, $form) {
             if (!responseText.error) {
                var designer = APIDesigner();
                designer.saved_api = {};
                designer.saved_api.name = responseText.data.apiName;
                designer.saved_api.version = responseText.data.version;
                designer.saved_api.provider = responseText.data.provider;                
                $('#saveMessage').hide();
                $('#saveButtons').show();                
                $( "body" ).trigger( "api_saved" );                             
             } else {
                 if (responseText.message == "timeout") {
                     if (ssoEnabled) {
                         var currentLoc = window.location.pathname;
                         if (currentLoc.indexOf(".jag") >= 0) {
                             location.href = "index.jag";
                         } else {
                             location.href = 'site/pages/index.jag';
                         }
                     } else {
                         jagg.showLogin();
                     }
                 } else {
                     jagg.message({content:responseText.message,type:"error"});
                 }
                 $('#saveMessage').hide();
                 $('#saveButtons').show();
             }
            }, dataType: 'json'
        });
        }
    });*/
    
    $("#prototyped_api").click(function(e){
        $("body").on("api_saved", function(e){
            $("body").unbind("api_saved");    
                var designer = APIDesigner();            
                $.ajax({
                    type: "POST",
                    url: jagg.site.context + "/site/blocks/life-cycles/ajax/life-cycles.jag",
                    data: {
                        action :"updateStatus",
                        name:designer.saved_api.name,
                        version:designer.saved_api.version,
                        provider: designer.saved_api.provider,
                        status: "PROTOTYPED",
                        publishToGateway:true,
                        requireResubscription:true
                    },
                    success: function(responseText){
                        if (!responseText.error) {
                            jagg.message({content:"API deployed as a Prototype.",type:"info"});
                        }else{
                             if (responseText.message == "timeout") {
                                 if (ssoEnabled) {
                                     var currentLoc = window.location.pathname;
                                     if (currentLoc.indexOf(".jag") >= 0) {
                                         location.href = "index.jag";
                                     } else {
                                         location.href = 'site/pages/index.jag';
                                     }
                                 } else {
                                     jagg.showLogin();
                                 }
                             } else {
                                 jagg.message({content:responseText.message,type:"error"});
                             }
                        }
                    },
                    dataType: "json"
                });               
            });
            $("#implement_form").submit();                        
        });
    /*$.validator.addMethod('contextExists', function(value, element) {
        if (value.charAt(0) != "/") {
            value = "/" + value;
        }
        var contextExist = false;
        var oldContext=$('#spanContext').text();
        jagg.syncPost("/site/blocks/item-add/ajax/add.jag", { action:"isContextExist", context:value,oldContext:oldContext },
                      function (result) {
                          if (!result.error) {
                              contextExist = result.exist;
                          }
                      });
        return this.optional(element) || contextExist != "true";
    }, 'Duplicate context value.');

    $.validator.addMethod('apiNameExists', function(value, element) {
        var apiNameExist = false;
        jagg.syncPost("/site/blocks/item-add/ajax/add.jag", { action:"isAPINameExist", apiName:value },
                      function (result) {
                          if (!result.error) {
                              apiNameExist = result.exist;
                          }
                      });
        return this.optional(element) || apiNameExist != "true";
    }, 'Duplicate api name.');

    $.validator.addMethod('selected', function(value, element) {
        return value!="";
    },'Select a value for the tier.');

    $.validator.addMethod('validRegistryName', function(value, element) {
        var illegalChars = /([~!@#;%^*+={}\|\\<>\"\'\/,])/;
        return !illegalChars.test(value);
    }, 'Name contains one or more illegal characters  (~ ! @ #  ; % ^ * + = { } | &lt; &gt;, \' / " \\ ) .');

    $.validator.addMethod('noSpace', function(value, element) {
        return !/\s/g.test(value);
    },'Name contains white spaces.');

    $.validator.addMethod('validInput', function(value, element) {
        var illegalChars = /([<>\"\'])/;
        return !illegalChars.test(value);
    }, 'Input contains one or more illegal characters  (& &lt; &gt; \'  " ');

    $.validator.addMethod('validateRoles', function(value, element) {
        var valid = false;
        var oldContext=$('#spanContext').text();
        jagg.syncPost("/site/blocks/item-add/ajax/add.jag", { action:"validateRoles", roles:value },
                      function (result) {
                          if (!result.error) {
                              valid = result.response;
                          }
                      });
        return this.optional(element) || valid == true;
    }, 'Invalid role name[s]');

    $.validator.addMethod('validateEndpoints', function (value, element){
        return APP.is_production_endpoint_specified() || APP.is_sandbox_endpoint_specified();
    }, 'A Production or Sandbox URL must be provided.');
    
    $.validator.addMethod('validateProdWSDLService', function (value, element){
        if (APP.is_production_endpoint_specified()) {
            return APP.is_production_wsdl_endpoint_service_specified();
        } 
        return true;        
    }, 'Service Name must be provided for WSDL endpoint.');
    
    $.validator.addMethod('validateProdWSDLPort', function (value, element){
        if (APP.is_production_endpoint_specified()) {
            return APP.is_production_wsdl_endpoint_port_specified();
        } 
        return true;   
    }, 'Service Port must be provided for WSDL endpoint.');
    
    $.validator.addMethod('validateSandboxWSDLService', function (value, element){
        if (APP.is_sandbox_endpoint_specified()) {
            return APP.is_sandbox_wsdl_endpoint_service_specified();
        }
        return true;
    }, 'Service Name must be provided for WSDL endpoint.');
    
    $.validator.addMethod('validateSandboxWSDLPort', function (value, element){
        if (APP.is_sandbox_endpoint_specified()) {
            return APP.is_sandbox_wsdl_endpoint_port_specified();
        }
        return true;
    }, 'Service Port must be provided for WSDL endpoint.');

    $.validator.addMethod('validateImageFile', function (value, element) {
        if ($(element).val() == "") {
            return true;
        }
        else {
            var validFileExtensions = ["jpg", "jpeg", "bmp", "gif", "png"];
            var ext = $(element).val().split('.').pop().toLowerCase();
            return ($.inArray(ext, validFileExtensions)) > -1;
        }
        return true;
    }, 'File must be in image file format.');

        $( "body" ).delegate( "button.check_url_valid", "click", function() {
        var btn = this;
        var url = $(this).parent().find('input:first').val();
        var type = $.parseJSON($("#endpoint_config").val())['endpoint_type']
        $(btn).parent().parent().find('.url_validate_label').remove();
        $(btn).addClass("loadingButton-small");
        $(btn).val(i18n.t('validationMsgs.validating'));

        if (url == '') {
            $(btn).parent().after(' <span class="label label-important url_validate_label"><i class="icon-remove icon-white"></i>'+ i18n.t('validationMsgs.invalid')+'</span>');
            var toFade = $(btn).next();
            $(btn).removeClass("loadingButton-small");
            $(btn).val(i18n.t('validationMsgs.testUri'));
            var foo = setTimeout(function(){$(toFade).hide()},3000);
            return;
        }
        if (!type) {
            type = "";
        }
        jagg.post("/site/blocks/item-add/ajax/add.jag", { action:"isURLValid", type:type,url:url },
                  function (result) {
                      if (!result.error) {
                          if (result.response == "success") {
                              $(btn).parent().after(' <span class="label label-success url_validate_label"><i class="icon-ok icon-white"></i>'+ i18n.t('validationMsgs.valid')+'</span>');

                          } else {
                              $(btn).parent().after(' <span class="label label-important url_validate_label"><i class="icon-remove icon-white"></i>'+ i18n.t('validationMsgs.invalid')+'</span>');
                          }
                          var toFade = $(btn).parent().parent().find('.url_validate_label');
                          var foo = setTimeout(function() {
                                $(toFade).hide();
                          }, 3000);

                      }
                      $(btn).removeClass("loadingButton-small");
                      $(btn).val(i18n.t('validationMsgs.testUri'));
                  }, "json");

    });*/


    });
});