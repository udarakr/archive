asset.server = function(ctx){
	return {
		endpoints : {
			apis: [{
				url: 'documents',
				path: 'documents.jag'
			}],
			pages: [{
				title : 'Documentation',
				url: 'documentation',
				path: 'documentation.jag'
			}]
		}
	};
};

asset.renderer = function(ctx){
    return {
        pageDecorators:{
            documents:function(page){
                if(page.assets.id){
                    var button = {};
                    button.name = 'Documentation';
                    button.url = '/asts/api/documentation/'+page.assets.id;
                    page.leftNav.push(button);
                }
            }
        }
    }

};