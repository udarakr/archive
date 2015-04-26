<script id="designer-apidoc-template" type="text/x-handlebars-template">
</script>

<script id="designer-resource-template" type="text/x-handlebars-template">
<div class="resource_body_padding">
    <h5>Implementation Notes :</h5>
    <a class="notes" data-path="{{resource_path}}" data-attr="notes">{{ notes }}</a>
    <br />
    <br />        
    <h5>Response Content Type : <a href="#" data-path="{{resource_path}}" data-attr="content_type" class="content_type" data-type="typeahead" data-pk="1" data-title="Responce Content Type">{{ content_type }}</a></h5>

    <br />    
    <h5>Parameters :</h5>
    {{#if parameters}}
    <table class="table table-condensed table-hover table-bordered">
        <tr>
        <th width="200px">Parameter Name</th>
        <th>Description</th>
        <th width="100px">Parameter Type</th>
        <th width="100px">Data Type</th>
        <th width="100px">Required</th>            
        </tr>     
    {{#each parameters}}
        <tr>
        <td>{{ name }}</td>
        <td>{{ description }}</td>
        <td>{{ paramType }}</td>
        <td>{{ type }}</td>
        <td>{{ required }}</td>
        </tr> 
    {{/each}}
    {{/if}}
    </table>      
    <h5>Script :</h5>
    <textarea class="editor span12" width="100%" style="height:150px;">{{#if mediation_script }}{{ mediation_script }}{{else}}/* mc.setProperty('CONTENT_TYPE', 'application/json');
    {{#each parameters}}var {{ name }} = mc.getProperty('uri.var.{{ name }}');
{{/each}}
mc.setPayloadJSON('{ "data" : "sample JSON"}');*/
//Un-comment the above to send a sample responce.{{/if}}
</textarea>
</div>  
</script>


<script id="designer-resources-template" type="text/x-handlebars-template">
<fieldset class="<%= hide_impl %> implementation_method_inline implementation_method">
<legend>Resources</legend>
<table style="width:100%">                       
{{#each api_doc.apis}}
{{ setIndex @index}}
    <tr>
	<td colspan="3"><%=i18n.localize("inlineEphelp")%></td>
    </tr>
    <tr>
    <td colspan="3"><h4 class="resource_group_title">{{ path }}</h4></td>
    </tr>
    {{# each file.apis}}
        {{ setIndex @index}}
        {{# each operations}}
        <tr class="resource_container" data-path="$.apis[{{ ../../index}}].file.apis[{{ ../index }}].operations[{{ @index }}]">
            <td class="resource-method-td resource_expand" data-path="$.apis[{{ ../../index}}].file.apis[{{ ../index }}].operations[{{ @index }}]">
                <span class=" resource-method resource-method-{{ method }}">{{ method }}</span>
            </td>
            <td class="resource_expand"><a class="resource-path">{{ ../path }}</a></td>    
            <td  width="99%"><span class="operation-summary change_summary" data-path="$.apis[{{ ../../index}}].file.apis[{{ ../index }}].operations[{{ @index }}]" data-attr="summary" >{{ summary }}</span></td>
        </tr>
        <tr><td colspan="3" class="resource_body hide" data-path="$.apis[{{ ../../index}}].file.apis[{{ ../index }}].operations[{{ @index }}]"></td></tr>
        {{/each}}
    {{/each}}

{{/each}}
</table>                           
</fieldset>                       
</script>

<script>
    $(document).ready(function(){        
        $.get( "<%= jagg.url("/site/blocks/item-design/ajax/add.jag") %>?name=<%= encode.forHtml(api.name) %>&version=<%= encode.forHtml(api.version) %>&provider=<%= encode.forHtml(api.provider) %>&action=swagger" , function( data ) {
            var data = jQuery.parseJSON(data);
            var designer = APIDesigner();
            designer.load_api_document(data);
            $("#swaggerUpload").modal('hide');
        });

        $('#go_to_manage').click(function(e){            
            $("body").on("api_saved", function(e){
                location.href = "<%= jagg.url("/manage?"+apiUrlId)%>";
            });
            $("#implement_form").submit();
        });        
    })
</script>