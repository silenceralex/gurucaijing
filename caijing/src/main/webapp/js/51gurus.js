(function(window, undefined){
   var $ = window.jQuery, Config = window.Config || {}, WB = window.WB || {};
   cart = {
      init : function ( data, container ) {
         var t = this;
         t.dataArr = data;
         t.container = container;
         t.draw();
      },
      draw : function () {
         var t = this;
         var chart1option = {
            chart: {
               renderTo: t.container
            },
            credits : {
               enabled:false
            },
            legend: {
               x: -100,
               borderWidth: 0,
               backgroundColor: '#FFFFFF'
            },
            title: {
               text: ''
            },
            tooltip: {
               crosshairs: true,
               formatter: function() {
                  return '<b>'+ this.series.name +'</b><br/>'+
                  this.x +': '+ this.y +'%';
               }
            },
            xAxis: {
               type:"datetime",//ʱ����Ҫ�������type��Ĭ����linear
               maxPadding : 0.05,
               minPadding : 0.05,
               //tickInterval : 24 * 3600 * 1000 * 2,//���컭һ��x�̶�
               //����150px��һ��x�̶ȣ�����������Ǹ�һ�������ˣ��������ļ��Ϊ׼
               tickPixelInterval : 150,
               tickWidth:5,//�̶ȵĿ��
               lineColor : '#990000',//�Զ���̶���ɫ
               lineWidth :3,//�Զ���x����
               gridLineWidth :0,//Ĭ����0������ͼ��û����������
               //�Զ���x�̶�����ʾ��ʱ���ʽ�����ݼ����С��������Ԥ���Сʱ/����/�յĸ�ʽ����ʾ
               dateTimeLabelFormats:
               {
                  second: '%H:%M:%S',
                  minute: '%e. %b %H:%M',
                  hour: '%m-%d %H:%M',
                  day: '%m-%d',
                  week: '%m-%d',
                  month: '%y %m',
                  year: '%Y'
               }
            },
            yAxis: {
               title: {
                  text: ''
               },
               labels: {
                  formatter: function() {
                     return this.value +'%'
                  }
               }
            },
            //�����ԣ����ܰ�ʱ��ֵ�ŵ�categories�����ŵ�series��data����ȥ
            //�����ǲ��еģ�categories:[1274457600000,1274544000000,1274630400000]
            //ʱ�䵥λ�Ǻ��룬Unixʱ�������1000
            series : [{
               name : t.dataArr[0].name,
               data : t.dataArr[0].data
            },
            {
               name : t.dataArr[1].name,
               data : t.dataArr[1].data
            }]
         };

         $(document).ready(function() {
            chart1= new Highcharts.Chart(chart1option);
         });
      }
   };
   showAnaly = function( num, dataId, name1, name2 ) {//data : [{name:"xxx", data:[[xxx,xx],[xxx,xx]]},{name:"xxx", data:[[xxx,xx],[xxx,xx]]}]
      var data = [],
          tmp = [],
          tmpArr = [],
          tmpArr2 = [];
      var tmpobj = {},
          tmpobj2 = {};
      for (var i = 0; i < $("#" + dataId + " th").length; i++) {
         tmp = [];
         tmp[0] = Number($("#" + dataId + " th:eq(" + i + ")").html().replace(/\s/mg,""));
         tmp[1] = Number($("#" + dataId + " tr:eq(1) td:eq(" + i + ")").html().replace(/\s/mg,"").split(",")[1].split("%")[0]);
         tmpArr.push( tmp );
         tmp = [];
         tmp[0] = Number($("#" + dataId + " th:eq(" + i + ")").html().replace(/\s/mg,""));
         tmp[1] = Number($("#" + dataId + " tr:eq(2) td:eq(" + i + ")").html().replace(/\s/mg,"").split(",")[1].split("%")[0]);
         tmpArr2.push( tmp );
      }
      tmpobj.name = name1;
      tmpobj.data = tmpArr;
      tmpobj2.name = name2;
      tmpobj2.data = tmpArr2;
      data.push(tmpobj,tmpobj2);
      cart.init( data, "star_detail_preview" );
      $("#test").show();
   };
   hide = function( id ) {
      $( "#" + id ).hide();
   };
})(window);