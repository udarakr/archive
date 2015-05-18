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