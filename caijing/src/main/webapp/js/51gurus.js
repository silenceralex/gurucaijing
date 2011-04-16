(function(window, undefined){
   var $ = window.jQuery, Config = window.Config || {}, WB = window.WB || {};
   chart = {
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
                  Highcharts.dateFormat('%Y年%m月%d日', this.x) +': '+ this.y +'%';
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
               tickPixelInterval : 60,
               tickWidth:1,//刻度的宽度
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
                  month: '%m-%d',
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
   getData = function ( dataId, name1, name2 ) {
      var data = [],
          dateArr = [],
          tmpArr = [],
          tmpArr2 = [],
          tmp = [];
      var tmpobj = {},
          tmpobj2 = {};
      var date = "";
      var pos = {};
      $("#" + dataId + " th").each(function(i){
         dateArr.push( Number(this.innerHTML.replace(/\s/mg,"")) );
         return dateArr;
      });
      $("#" + dataId + " tr:eq(1) td").each(function(i){
         tmpArr.push( Number(this.innerHTML.replace(/\s/mg,"").split(",")[1].replace("%", "")) );
      });
      $("#" + dataId + " tr:eq(2) td").each(function(i){
         tmpArr2.push( Number(this.innerHTML.replace(/\s/mg,"").split(",")[1].replace("%", "")) );
      });
      tmpobj.name = name1;
      tmpobj.data = [];
      tmpobj2.name = name2;
      tmpobj2.data = [];
      for( var i = 0; i < dateArr.length; i ++ ) {
         tmp.push( dateArr[i], tmpArr[i] );
         tmpobj.data.push( tmp );
         tmp = [];
         tmp.push( dateArr[i], tmpArr2[i] );
         tmpobj2.data.push( tmp );
         tmp = [];
      }
      data.push( tmpobj,tmpobj2 );
      return data;
   }
   showAnaly = function( senderId, dataId, name1, name2 ) {//data : [{name:"xxx", data:[[xxx,xx],[xxx,xx]]},{name:"xxx", data:[[xxx,xx],[xxx,xx]]}]
      if( !document.getElementById("analyLayer") ) {
         var elem = document.createElement("div");
         elem.setAttribute("id", "analyLayer");
         elem.innerHTML = "<div id='analyHolder'></div><div class='close'><a href='javascript:;' onclick='hide(\"analyLayer\")'>关闭</a></div>";
         document.body.appendChild( elem );
      }
      var pos = getPosition( document.getElementById(senderId) );
      $("#analyLayer").css("left", pos.x + 50 + "px");
      $("#analyLayer").css("top", pos.y + "px");
      $("#analyLayer").fadeIn("slow");
      var data = getData(dataId, name1, name2);
      chart.init( data, "analyHolder" );
      
   };
   hide = function( id ) {
      $( "#" + id ).fadeOut("fast");
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
      return { "x" : x, "y" : y + document.body.scrollTop };
   };
   cart = {
      init : function ( user, orderId ) {
         var t = this;
         t.user = user;
         t.orderId = orderId;
         t.cartArr = [];
         Rookie(function(){
            if ( !this.read("orderId") ) {
               t.orderId = +new Date();
               this.write( 'orderId', t.orderId );
            } else if ( t.orderId >= this.read("orderId") ) {
               alert( "应该清空本地数据" );
               t.clear();
            } else {
               t.cartArr = this.read('cart');
            };
         });
      },
      clear : function () {
         var t = this;
         Rookie(function(){
            this.clear("cart");
            this.clear("orderId");
         });
      },
      add : function ( id, title, type, num, price ) {
         var t = this,
         isExist = false;
         for ( var i = 0; i < t.cartArr; i ++ ) {
            alert(id + "," + t.cartArr[i].id);
            if ( id == t.cartArr[i].id ) {
               isExist = true;
               alert("is exist");
            }
         };
         if( isExist ) {
            alert("exist!");
            return;
         };
         t.cartArr.push({'id' : id, 'title' : title, 'type' : type, 'num' : num, 'price' : price});
         Rookie(function(){
            this.write( 'cart', t.cartArr );
         });
      },
      del : function ( id ) {
         for ( var i = 0; i < t.cartArr; i ++ ) {
            if ( id == t.cartArr[i].id ) {
               t.cartArr.splice( i, 1 );
            }
         };
         Rookie(function(){
            this.write( 'cart', t.cartArr );
         });
      },
      motify : function ( id, field, value ) {
         for ( var i = 0; i < t.cartArr; i ++ ) {
            if ( id == t.cartArr[i].id ) {
               t.cartArr[i][field] = value;
            }
         };
         Rookie(function(){
            this.write( 'cart', t.cartArr );
         })
      },
      show : function () {
         Rookie(function(){
            console.log(this.read('cart'));
         })
      },
      enter : function () {
         
      },
      back : function () {
         
      },
      popDialog : function ( num, price ) {
         var elem = $("#dialogS")
         $( elem ).fadeIn("slow");
         $( elem ).css({ 
            position : 'absolute',
            left : ($(window).width() - $( elem ).outerWidth())/2,
            top : ($(window).height() - $( elem ).outerHeight())/2
         });
      }
   };
})(window);