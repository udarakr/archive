var render = function(theme, data, meta, require) {
    theme('single-col-fluid', {
        title: 'Documents',
        header: [{
            partial: 'header',
            context: data
        }]
    });
};