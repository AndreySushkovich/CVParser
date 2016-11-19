new Chartist.Pie("#clientServer .chart", {
        labels: ["Frontend", "Backend"],
        series: [{
            value: 70,
            name: "Frontend",
            className: "ct-series-b clientServer--frontend",
        }, {
            value: 30,
            name: "Backend",
            className: "ct-series-a clientServer--backend",
            meta: "Meta 1"
        }]
    }, {
        donut: true,
        donutWidth: 60,
        startAngle: 270,
        total: 200,
        showLabel: true
    }
);

new Chartist.Line('#career .chart', {
    labels: ["2011", "2012", "2013", "2014", "2015", "2016"],
    series: [
        [1, 1, 2, 3, 4, 3],
    ]
}, {
    low: 0,
    showArea: true,
    chartPadding: {
        right: 40
    },
    axisY: {
        labelInterpolationFnc: function(value) {
            switch (value) {
                case 1: return "Intern/Junior";
                case 2: return "Middle";
                case 3: return "Senior";
                case 4: return "Lead";
                default: return "";
            }
        }
    }
});

new Chartist.Pie('#frontend .chart', {
    series: [5, 3, 4, 1, 5, 9],
    labels: ["jQuery", "React", "CSS", "AngularJS", "Canvas", "HTML"]
});

new Chartist.Pie('#backend .chart', {
    series: [2, 1, 2, 5, 9, 3],
    labels: ["Java", "VCS", "Apache", "Servers", "DBs", "Linux"]
});
