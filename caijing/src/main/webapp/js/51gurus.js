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
      // 初始化购物车
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
      // 清除购物车
      clear : function () {
         var t = this;
         Rookie(function(){
            this.clear("cart");
            this.clear("orderId");
         });
      },
      /* 
      // 添加一个产品
      add : function ( id, title, type, num, price ) {
         var t = this,
         isExist = false;
         var total = { num : 0, price : 0 };
         for ( var i = 0; i < t.cartArr; i ++ ) {
            total.num += t.cartArr[i].num;
            total.price += t.cartArr[i].num * t.cartArr[i].price;
            if ( id == t.cartArr[i].id ) {
               isExist = true;
            }
         };
         if( isExist ) {
            alert("不能重复添加");
            return;
         };
         total.num += num;
         total.price += num * price;
         t.cartArr.push({'id' : id, 'title' : title, 'type' : type, 'num' : num, 'price' : price});
         Rookie(function(){
            this.write( 'cart', t.cartArr );
         });
         t.popDialog( total.num, total.price );
      }, 
      */
      // 添加一个产品
      add : function ( id, num, price ) {
         var t = this,
         isExist = false;
         var total = { num : 0, price : 0 };
         for ( var i = 0; i < t.cartArr; i ++ ) {
            total.num += t.cartArr[i].num;
            total.price += t.cartArr[i].num * t.cartArr[i].price;
            if ( id == t.cartArr[i].id ) {
               isExist = true;
            }
         };
         if( isExist ) {
            alert("不能重复添加");
            return;
         };
         total.num += num;
         total.price += num * price;
         t.cartArr.push({'id' : id, 'num' : num, 'price' : price});
         Rookie(function(){
            this.write( 'cart', t.cartArr );
         });
         t.popDialog( total.num, total.price );
         if ( $("#cartTb") ) {
            $("#cartTb").append('\
               <tr id="' + id + '">\
                  <td>' + id + '</td>\
                  <td>' + t.pObj[id].title + '</td>\
                  <td>' + t.pObj[id].intro + '</td>\
                  <td><span onclick="cart.sub(\'' + id + '\')">-</span><span id="' + id + 'num">' + num + '</span><span onclick="cart.plus(\'' + id + '\')">+</span></td>\
                  <td><span class="price">' + price + '</span>元</td>\
                  <td><a onclick="cart.del(' + id + ')" href="javascript:;">删除</a></td>\
               </tr>\
            ');
         }
      },
      // 删除某个产品
      del : function ( id ) {
         for ( var i = 0; i < t.cartArr; i ++ ) {
            if ( id == t.cartArr[i].id ) {
               t.cartArr.splice( i, 1 );
            }
         };
         $( "#" + id ).remove(); // 删除该节点
         Rookie(function(){
            this.write( 'cart', t.cartArr );
         });
      },
      // 产品个数加一
      plus : function ( id ) {
         var t = this,
             num = Number($( "#" + id + "num" ).text());
         num += 1;
         $( "#" + id + "num" ).text( num );
         t.motify( id, "num", num );
      },
      // 产品个数减一
      sub : function ( id ) {
         var t = this,
             num = Number($( "#" + id + "num" ).text());
         if( num <= 1 ) {
            return;
         }
         num -= 1;
         $( "#" + id + "num" ).text( num);
         t.motify( id, "num", num );
      },
      // 修改数据
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
      // 展示数据
      show : function () {
         /**
          * pObj: {0002:{id:0002,title:"分析师成功率",intro:"套餐",price:10},0003:{...}}
          */
         var t = this,
             cartTb = $("#cartTb"),
             pid = "", // 产品id
             num = 1, // 产品数量
             price = 0, // 价格
             totalN = 0, // 总数量
             totalP = 0; // 总价格
         Rookie(function(){
            t.cartArr = this.read('cart');
         })
         for ( var i = 0; i < t.cartArr.length; i++ ) {
            pid = t.cartArr[i].id;
            num = t.cartArr[i].num;
            price = t.pObj[pid].price * num;
            totalN += num;
            totalP += price;
            $( cartTb ).append('\
               <tr id="' + pid + '">\
                  <td>' + pid + '</td>\
                  <td>' + t.pObj[pid].title + '</td>\
                  <td>' + t.pObj[pid].intro + '</td>\
                  <td><span onclick="cart.sub(\'' + pid + '\')">-</span><span id="' + pid + 'num">' + num + '</span><span onclick="cart.plus(\'' + pid + '\')">+</span></td>\
                  <td><span class="price">' + price + '</span>元</td>\
                  <td><a onclick="cart.del(' + pid + ')" href="javascript:;">删除</a></td>\
               </tr>\
            ');
         };
         $("#totalN").text( totalN );
         $("#totalP").text( totalP );
      },
      // 弹出提示框
      popDialog : function ( num, price ) {
         var elem = $("#dialogS");
         $("#tipTotalN").innerHTML = num;
         $("#tipTotalP").innerHTML = price;
         $( elem ).fadeIn("slow");
         $( elem ).css({ 
            position : 'absolute',
            left : ($(window).width() - $( elem ).outerWidth())/2,
            top : ($(window).height() - $( elem ).outerHeight())/2
         });
      }
   };
   userInfo = {
      nickReg : /^[\w_]{4,16}$/,
      emailReg : /\w+([-+.]\w+)*@\w+([-.]\w+)*\.\w+([-.]\w+)*/,
      mobileReg : /^1{1}\d{10}$/,
      passReg : /^.{6,16}$/,
      init : function () {
         var t = this;
      }
   };
   reg = {
      nickReg : /^[\w_]{4,16}$/,
      emailReg : /\w+([-+.]\w+)*@\w+([-.]\w+)*\.\w+([-.]\w+)*/,
      passReg : /^.{6,16}$/,
      emailNode : $("#email"),
      pass1Node : $("#pass1"),
      pass2Node : $("#pass2"),
      nicknameNode : $("#nickname"),
      emailTipNode : $("#emailTip"),
      pass1TipNode : $("#pass1Tip"),
      pass2TipNode : $("#pass2Tip"),
      nicknameTipNode : $("#nicknameTip"),
      // 初始化
      init : function () {
         var t = this;
         // 绑定email验证事件
         $( t.emailNode ).bind("blur", function () {
            t.checkEmail();
         });
         $( t.emailNode ).bind("focus", function () {
            $( t.emailTipNode ).fadeOut("slow");
         });
         // 绑定密码验证事件
         $( t.pass1Node ).bind("blur", function () {
            t.checkPass();
         });
         $( t.pass1Node ).bind("focus", function () {
            $( t.pass1TipNode ).fadeOut("slow");
         });
         // 绑定密码验证事件
         $( t.pass2Node ).bind("blur", function () {
            t.checkPass2();
         });
         $( t.pass2Node ).bind("focus", function () {
            $( t.pass2TipNode ).fadeOut("slow");
         });
         // 绑定昵称验证事件
         $( t.nicknameNode ).bind("blur", function () {
            t.checkNick();
         });
         $( t.nicknameNode ).bind("focus", function () {
            $( t.nicknameTipNode ).fadeOut("slow");
         });
      },
      // 通用检查表单
      checkVal : function ( reg, node, tip ) {
         var t = this,
             val = $( node ).text();
         
         if ( val && reg.test( val ) ) {
            $( tip ).fadeOut("slow");
            return true;
         } else {
            $( tip ).fadeIn("slow");
            return false;
         };
      },
      // 验证邮件格式
      checkEmail : function () {
         var t = this;
         t.checkVal( t.emailReg, $( t.emailNode ), $( t.emailTipNode ));
      },
      // 验证密码格式
      checkPass : function () {
         var t = this;
         t.checkVal( t.passReg, $( t.pass1Node ), $( t.pass1TipNode ));
      },
      // 验证两次密码输入
      checkPass2 : function () {
         var t = this;
         if ( t.pass1Node.text() != t.pass2Node.text() ) {
            t.pass2TipNode.fadeIn("slow");
            return false;
         } else {
            t.pass2TipNode.fadeOut("slow");
            return true;
         };
      },
      // 验证昵称
      checkNick : function () {
         var t = this;
         t.checkVal( t.passReg, $( t.nicknameNode ), $( t.nicknameTipNode ));
      },
      // 验证表单并提交
      formSubmit : function () {
         var t = this;
         if ( t.checkEmail && t.checkPass() &&t.checkPass2() && t.checkNick() ) {
            document.reg.submit();
         };
      }
   };
})(window);