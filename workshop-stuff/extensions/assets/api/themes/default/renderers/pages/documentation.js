var render = function(theme, data, meta, require) {
    theme('single-col-fluid', {
        title: 'Documentation',
        header: [{
            partial: 'header',
            context: data
        }],
        ribbon: [{
            partial: 'ribbon',
            context: data
        }],
        listassets: [{
            partial: 'documents-body',
            context: data
        }]
    });
};