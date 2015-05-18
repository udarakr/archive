$(function(){
        alert('script loaded!');
    $.ajax({
        url:caramel.url('/asts/api/apis/documents'),
        success:function(data){
            //alert('Data '+data);
            $('#document-list').html(data);
        },
        error:function(){
            alert('Failed to load the docs');
        }
    });
});