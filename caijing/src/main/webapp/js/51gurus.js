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
                  Highcharts.dateFormat('%m-%d', this.x) +': '+ this.y +'%';
               }
            },
            plotOptions: {
               line: {
                  marker: {
                     enabled: false,
                     symbol: 'circle',
                     radius: 2,
                     states: {
                        hover: {
                           enabled: true
                        }
                     }
                  }
               }
            },
            xAxis: {
               type:"datetime",//时间轴要加上这个type，默认是linear
               maxPadding : 0.05,
               minPadding : 0.05,
               //tickInterval : 24 * 3600 * 1000 * 2,//两天画一个x刻度
               //或者150px画一个x刻度，如果跟上面那个一起设置了，则以最大的间隔为准
               tickPixelInterval : 150,
               tickWidth:5,//刻度的宽度
               lineColor : '#990000',//自定义刻度颜色
               lineWidth :3,//自定义x轴宽度
               gridLineWidth :0,//默认是0，即在图上没有纵轴间隔线
               //自定义x刻度上显示的时间格式，根据间隔大小，以下面预设的小时/分钟/日的格式来显示
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
            //经测试，不能把时间值放到categories里，必须放到series的data里面去
            //这样是不行的：categories:[1274457600000,1274544000000,1274630400000]
            //时间单位是毫秒，Unix时间戳乘上1000
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
   showAnaly = function( senderId, dataId, name1, name2 ) {//data : [{name:"xxx", data:[[xxx,xx],[xxx,xx]]},{name:"xxx", data:[[xxx,xx],[xxx,xx]]}]
      if( !document.getElementById("analyLayer") ) {
         var elem = document.createElement("div");
         elem.setAttribute("id", "analyLayer");
         elem.innerHTML = "<div id='analyHolder'></div><div class='close'><a href='javascript:;' onclick='hide(\"analyLayer\")'>关闭</a></div>";
         document.body.appendChild( elem );
      }
      var data = [],
          tmp = [],
          tmpArr = [],
          tmpArr2 = [];
      var tmpobj = {},
          tmpobj2 = {};
      var pos = {};
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
      cart.init( data, "analyHolder" );
      pos = getPosition( document.getElementById(senderId) );
      $("#analyLayer").css("left", pos.x + 30 + "px");
      $("#analyLayer").css("top", pos.y + "px");
      $("#analyLayer").show();
   };
   hide = function( id ) {
      $( "#" + id ).hide();
   };
   getPosition = function ( sender ) {
      var e = sender,E = e;
      var x = e.offsetLeft;
      var y = e.offsetTop;
      while ( e = e.offsetParent ) {
         var P = e.parentNode;
         while (P != ( E = E.parentNode ) ) {
            x -= E.scrollLeft;
            y -= E.scrollTop;
      }
      x += e.offsetLeft;
      y += e.offsetTop;
      E = e;
      }
      return { "x" : x, "y" : y };
   };
})(window);