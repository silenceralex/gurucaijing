(function(window, undefined){
   var $ = window.jQuery, Config = window.Config || {}, WB = window.WB || {};
   chart = {
      //for(i=1;i<=industry.length;i++){industry[i-1].id=i}
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
      pObj : {},
      recommend : [],
      industry : {},
      masterArr : {},
      init : function ( action ) {
         var t = this;
         t.cartArr = [];
         /* t.getIndustry();
         t.getMaster();
         t.getProducts( action ); */
         t.getInterface( action );
         
         //t.initIndustrySelect();
         // t.buyBind();
         Rookie(function(){
            var cartItem = this.read('cart');
            if( cartItem ) {
               t.cartArr = cartItem;
            } else {
               this.write( 'cart', t.cartArr );
            }
         })
      },
      /* getProducts : function ( action ) {
         var t= this;
         $.getJSON('/get/product.do', function(data) {
            var products = data//eval('(' + data + ')');
            var i = 1;
            for ( n in products ) {
               //if ( products[n].name.indexOf("草根") ) {
               t.pObj["p" + products[n].pid] = products[n];
               //}
               i += 1;
            }
            if ( action == "showProducts") {
               t.drawProductsTable( t.pObj );
            }
         });
      }, */
      drawProductsTable : function ( obj ) {
         var t = this,
             industryStr = "",
             monthStr = "",
             tbStr = "<table width='99%' class='productsTable' cellspacing='0' cellpadding='0' border='1'>";
             tbStr += "<tr><th width='120'>产品</th><th>产品说明</th><th>建议价格(人民币)</th><th width='70'>操作</th></tr>";
         //for ( var i = 0; i < obj.length; i ++ ) {
         for ( n in obj ) {
            if ( !obj[n].name.indexOf("草根") ) {
               continue;
            }
            if ( obj[n].intro ) {
               t.recommend.push( obj[n] );
               continue;
            }
            tbStr += "<tr id='product" + obj[n].pid + "'>";
            tbStr += "<td>" + obj[n].name + "</td>";
            tbStr += "<td>" + obj[n].description + "</td>";
            if ( obj[n].isIndustry ) {
               industryStr = "/行业";
            } else {
               industryStr = "";
            }
            if ( obj[n].continuedmonth ) {
               monthStr = "月";
            } else {
               monthStr = "年";
            }
            tbStr += "<td>" + obj[n].price + "元/" + monthStr + industryStr + "</td>";
            tbStr += "<td><a class='cRed' href='javascript:;' onclick='cart.buy(" + obj[n].pid + ")'>放入购物车</a></td>";
            tbStr += "</tr>";
         }
         tbStr += "</table>";
         $("#products").html( tbStr );
      },
      /* getIndustry : function () {
         var t= this;
         $.getJSON('/get/industry.do', function(data) {
            var industry = data//eval('(' + data + ')');
            var i = 1;
            for ( n in industry ) {
               // t.industry["i" + i] = industry[n];
               t.industry[industry[n].industryid] = industry[n];
               i += 1;
            }
            t.initIndustrySelect( t.industry );
         });
      }, */
      // 获取所有接口
      getInterface : function ( action ) {
         var t = this,
             i = 1;
         $.getJSON('/get/interface.do', function( data ) {
            for ( n in data.product ) {
               t.pObj["p" + data.product[n].pid] = data.product[n];
               i += 1;
            }
            if ( action == "showProducts") {
               t.drawProductsTable( t.pObj );
            }
            for ( n in data.master ) {
               t.masterArr[data.master[n].masterid] = data.master[n];
            }
            t.renderMaster();
            i = 1;
            for ( n in data.industry ) {
               t.industry[data.industry[n].industryid] = data.industry[n];
               i += 1;
            }
            t.initIndustrySelect( t.industry );
         });
      },
      renderMaster : function () {
         var t = this,
             tbStr = "<p class='cart-live-p'>草根大师直播室:</p>";
         tbStr += "<table width='99%' class='productsTable' cellspacing='0' cellpadding='0' border='1'>";
         tbStr += "<tr><th width='100'>大师</th><th>大师介绍</th><th width='70'>操作</th></tr>";
         for ( n in t.masterArr ) {
            tbStr += "<tr>";
            tbStr += "<td>" + t.masterArr[n].mastername + "</td>";
            tbStr += "<td>" + t.masterArr[n].intro + "</td>";
            tbStr += "<td><a class='cRed' href='javascript:;' onclick='cart.buyLive(10, " + t.masterArr[n].masterid + " )'>加入购物车</a></td>";
            tbStr += "</tr>";
         }
         tbStr += "</table>";
         $("#products").append( tbStr );
      },
      /* getMaster : function () {
         var t = this;
         $.getJSON('/get/master.do', function(data) {
            for ( n in data ) {
               t.masterArr[data[n].masterid] = data[n];
            }
            //t.masterArr = data//eval( '(' + data + ')' );
            var tbStr = "<p class='cart-live-p'>草根大师直播室:</p>";
            tbStr += "<table width='99%' class='productsTable' cellspacing='0' cellpadding='0' border='1'>";
            tbStr += "<tr><th width='100'>大师</th><th>大师介绍</th><th width='70'>操作</th></tr>";
            for ( n in t.masterArr ) {
               tbStr += "<tr>";
               tbStr += "<td>" + t.masterArr[n].mastername + "</td>";
               tbStr += "<td>" + t.masterArr[n].intro + "</td>";
               tbStr += "<td><a class='cRed' href='javascript:;' onclick='cart.buyLive(10, " + t.masterArr[n].masterid + " )'>加入购物车</a></td>";
               tbStr += "</tr>";
            }
            tbStr += "</table>";
            $("#products").append( tbStr );
         });
      }, */
      delIndustryItem : function ( node ) {
         var fa = node.parentNode.parentNode,
             gfa = node.parentNode.parentNode.parentNode,
             itemArr = $(".pop-industry");
         if ( itemArr.length > 1 ) {
            gfa.removeChild(fa);
         }
         //node.parentNode.parentNode.style.display = "none";
         //$(node).parent().remove();
      },
      addIndustryItem : function () {
         /* var str = '<tr class="pop-industry">'+
              ' <td class="industcol"><select name="industry"></select></td>'+
               '<td>'+
                  '<input class="count" class="popNum" type="text" value="1"/>'+
               '</td>'+
               '<td><span id="chosePrice"></span>元</td>'+
               '<td class="industcol"><span class="operation plusBtn" onclick="cart.addIndustryItem()" title="增加一个行业">+</span></td>'+
            '</tr>'; */
         var temp = $(".pop-industry").last(),
             newNode = $(temp).clone();
         $( temp ).after( newNode );
      },
      initIndustrySelect : function ( industry ) {
         var t = this;
         //console.log( industry );
         for ( n in industry) {
            $("#dialogS .chose select").append("<option value='" + industry[n].industryid + "'>" + industry[n].industryname + "</option>");
         }
      },
      // 购买
      buy : function ( id, subId ) {
         var t = this;
         var id = "p" + id;
         
         t.popDialog();
         $("#dialogS .chose").show();
         $("#dialogS .success").hide();
         $("#chosePrice").text( t.pObj[id].price );
         document.getElementById("buyOk").onclick = function () {
            getTotal( id );
         }
         $("#0num").bind("blur", function () {
            var price = t.pObj[id].price * this.value;
            $("#chosePrice").text( price );
         });
         if ( t.pObj[id].isIndustry ) {
            $("#dialogS .chose .industcol").show();
            $("#dialogS .chose select").show();
         } else {
            $("#dialogS .chose .industcol").hide();
            $("#dialogS .chose select").hide();
         }
         function getTotal ( id ) {
            //$("#dialogS").fadeOut();
            /* 比较新
            var num = $("#0num").val(),
                industryId = $("#dialogS .chose select").val();
            if ( !t.pObj[id].isIndustry ) {
               industryId = "";
            }
            t.add( id, num, industryId ); */
            var items = $(".pop-count");
            for( var i = 0; i < items.length; i ++ ) {
               
               var num = $(".pop-count:eq(" + i + ")").val(),
                   industryId = $("#dialogS .chose select:eq(" + i + ")").val();
               if ( !t.pObj[id].isIndustry ) {
                  industryId = "";
               }
               t.add( id, num, industryId );
            }
            //var isOk = t.add( id, num, industryId );
         }
         return;
      },
      buyLive : function ( id, masterId ) {
         var t = this
             num = 1;
         if( t.add( "p" + id, num, masterId ) ) {
            t.popDialog();
         };
      },
      // 清除购物车
      clear : function () {
         var t = this;
         Rookie(function(){
            this.clear("cart");
         });
         // window.location.reload();
      },
      // 添加一个产品
      add : function ( id, num, industryId ) {
         //console.log("====" + id);
         var t = this,
             isExist = false,
             num = Number( num );
         var price = t.pObj[id].price;
         var total = { num : 0, price : 0 };
         for ( var i = 0; i < t.cartArr.length; i ++ ) {
            total.num += Number(t.cartArr[i].num);
            total.price += Number( t.cartArr[i].num ) * t.pObj[t.cartArr[i].id].price;
            if ( id == t.cartArr[i].id && industryId == t.cartArr[i].industryId ) {
               isExist = true;
            }
         };
         if( isExist ) {
            alert("不能重复添加");
            return false;
         };
         total.num += num;
         total.price += num * price;
         t.cartArr.push({'id' : id, 'num' : num, 'industryId' : industryId});
         //console.log(t.cartArr);
         Rookie(function(){
            this.write( 'cart', t.cartArr );
         });
         // 弹窗显示
         
         $("#tipTotalN").html( total.num );
         $("#tipTotalP").html( total.price );
         $("#dialogS .chose").hide();
         $("#dialogS .success").show();
         //t.popDialog();
            
         if ( $("#cartTb") ) {
            $("#cartList").show();
            $("#cartEmpty").hide();
            $("#cartTb").append('\
               <tr id="p' + id + '">\
                  <td>' + id + '</td>\
                  <td>' + t.pObj[id].title + '</td>\
                  <td>' + t.pObj[id].intro + '</td>\
                  <td><span onclick="cart.sub(\'' + id + '\')">-</span><span id="' + id + 'num">' + num + '</span><span onclick="cart.plus(\'' + id + '\')">+</span></td>\
                  <td><span class="price">' + price + '</span>元</td>\
                  <td><a onclick="cart.del(\'' + id + '\')" href="javascript:;">删除</a></td>\
               </tr>\
            ');
         }
         return true;
      },
      // 删除某个产品
      del : function ( id, subId ) {
         var t = this;
         var yes = confirm("是否要删除该产品？");
         if ( !yes ) {
            return;
         }
         $( "#" + id + subId ).remove(); // 删除该节点
         for ( var i = 0; i < t.cartArr.length; i ++ ) {
            if ( id == t.cartArr[i].id && subId == t.cartArr[i].industryId ) {
               t.cartArr.splice( i, 1 );
            }
         };
         t.getTotal();
         Rookie(function(){
            this.write( 'cart', t.cartArr );
         });
         t.getFormData();// 整理form的param数据
      },
      // 产品个数加一
      plus : function ( id, subId ) {
         var t = this,
             num = 0;
         subId = subId? subId: "";
         num = Number($( "#" + id + subId + "num" ).text());
         //num = Number($( "#" + id + "num" ).text());
         num += 1;
         $( "#" + id + subId + "num" ).text( num );
         t.motify( id, "num", num, subId );
         t.getFormData();// 整理form的param数据
      },
      // 产品个数减一
      sub : function ( id, subId ) {
         var t = this,
             num = 0;
         subId = subId? subId: "";
         num = Number($( "#" + id + subId + "num" ).text());
         if( num <= 1 ) {
            return;
         }
         num -= 1;
         $( "#" + id + subId + "num" ).text( num);
         t.motify( id, "num", num, subId );
         t.getFormData();// 整理form的param数据
      },
      // 修改数据
      motify : function ( id, field, value, subId ) {
         var t = this;
         for ( var i = 0; i < t.cartArr.length; i ++ ) {
            if ( id == t.cartArr[i].id && subId == t.cartArr[i].industryId ) {
               t.cartArr[i][field] = value;
            }
         };
         t.getTotal();
         Rookie(function(){
            this.write( 'cart', t.cartArr );
         })
      },
      // 展示数据
      show : function ( pay ) {
         /**
          * pObj: {0002:{id:0002,title:"分析师成功率",intro:"套餐",price:10},0003:{...}}
          */
         var t = this,
             cartTb = $("#cartTb"),
             pid = "", // 产品id
             num = 1, // 产品数量
             price = 0, // 价格
             totalN = 0, // 总数量
             totalP = 0, // 总价格
             pid = "",
             industryId = "";
         Rookie(function(){
            //console.log("dsfsdfdsf" + this.read('cart'));
            t.cartArr = this.read('cart');
            if ( !t.cartArr || t.cartArr.length < 1 ) {
               $("#cartList").hide();
               $("#cartEmpty").show();
               return;
            }
            showit( pay );
            t.getFormData();
            
         });
         function showit ( pay ) {
            for ( var i = 0; i < t.cartArr.length; i++ ) {
               pid = t.cartArr[i].id;
               num = Number( t.cartArr[i].num );
               industyId = t.cartArr[i].industryId;
               if ( t.pObj[pid] ) {
                  price = t.pObj[pid].price * num;
                  totalN += num;
                  totalP += price;
                  
                  // 分行业的
                  if( industyId ) {
                     var str = "";
                     if( !$("#"+pid).length ) {
                        str += '<tr id="' + pid + '">';
                        str += '<td><span id="' + pid + 'td1" class="pd-l-10" onclick="cart.expand(\'' + pid + '\')"><span title="展开" class="operation plusBtn">+</span></span></td>';
                        str += '<td>' + t.pObj[pid].name + '</td>';
                        str += '<td>' + t.pObj[pid].description + '</td>';
                        str += '<td>-</td>';
                        str += '<td>-</td>';
                        str += '<td>-</td>';
                        str += '</tr>';
                     }
                     str += '<tr class="' + pid + '-child hidden" id="' + pid + industyId + '">';
                     str += '<td><span class="pd-l-10">-</span></td>';
                     str += '<td>' + t.pObj[pid].name + '</td>';
                     if( t.industry[industyId] ) {
                        str += '<td>' + t.industry[industyId].industryname + '</td>';
                     } else {
                        //str += '<td>' + t.pObj[pid].description + '</td>';
                        str += '<td>' + t.masterArr[industyId].mastername + '</td>';
                     }
                     if ( pay ) {
                        str += '<td><span id="' + pid + industyId + 'num">' + num + '</span></td>';
                        str += '<td><span class="price">' + price + '</span>元</td>';
                     } else {
                        str += '<td><span class="operation subBtn" onclick="cart.sub(\'' + pid + '\',\'' + industyId + '\')">-</span><span id="' + pid + industyId + 'num">' + num + '</span><span class="operation plusBtn" onclick="cart.plus(\'' + pid + '\',\'' + industyId + '\')">+</span></td>';
                        str += '<td><span class="price">' + price + '</span>元</td>';
                        str += '<td><a onclick="cart.del(\'' + pid + '\',\'' + industyId + '\')" href="javascript:;">删除</a></td>';
                     }
                     
                  } else {
                     var str = '<tr id="' + pid + '">';
                     str += '<td><span class="pd-l-10">' + pid.split("p")[1] + '</span></td>';
                     str += '<td>' + t.pObj[pid].name + '</td>';
                     str += '<td>' + t.pObj[pid].description + '</td>';
                     if ( pay ) {
                        str += '<td><span id="' + pid + 'num">' + num + '</span></td>';
                        str += '<td><span class="price">' + price + '</span>元</td>';
                     } else {
                        str += '<td><span class="operation subBtn" onclick="cart.sub(\'' + pid + '\')">-</span><span id="' + pid + 'num">' + num + '</span><span class="operation plusBtn" onclick="cart.plus(\'' + pid + '\')">+</span></td>';
                        str += '<td><span class="price">' + price + '</span>元</td>';
                        str += '<td><a onclick="cart.del(\'' + pid + '\',\'\')" href="javascript:;">删除</a></td>';
                     }
                     
                  }
                  str += '</tr>';
                  
                  
                  $( cartTb ).append( str );
               }
            };
            $("#totalN").text( totalN );
            $("#totalP").text( totalP );
         }
      },
      // 展开
      expand : function ( id ) {
         var t = this,
             child1 = $("." + id + "-child")[0];
         if ( $(child1).hasClass("hidden") ) {
            $("#" + id + "td1").html('<span title="折叠" class="operation subBtn">-</span>');
            $("." + id + "-child").removeClass("hidden");
         } else {
            $("#" + id + "td1").html('<span title="展开" class="operation plusBtn">+</span>');
            $("." + id + "-child").addClass("hidden");
         }
         
      },
      // 整理数据格式，以便提交到后台
      getFormData : function () {
         var t = this,
             str = "[";
         for( x in t.cartArr ) {
            pid = t.cartArr[x].id.replace(/[a-z]*/, "");
            industryId = t.cartArr[x].industryId? t.cartArr[x].industryId: "";
            num = t.cartArr[x].num;
            str += '{productid:"' + pid + '", industryid:"' + industryId + '",num:"' + num + '"},'
         }
         str = str.substr(0,str.length-1);
         str += "]";
         $("#cartParam").val( str );
      },
      getTotal : function () {
         var t = this,
             num = 0,
             price = 0,
             pid = "",
             totalN = 0,
             totalP = 0;
         for ( var i = 0; i < t.cartArr.length; i++ ) {
            pid = t.cartArr[i].id;
            num = Number( t.cartArr[i].num );
            if ( t.pObj[pid] ) {
               price = t.pObj[pid].price * num;
               totalN += num;
               totalP += price;
            }
         };
         $("#totalN").text( totalN );
         $("#totalP").text( totalP );
      },
      // 弹出提示框
      popDialog : function () {
         var elem = $("#dialogS");
         $(".pop-industry:gt(0)").remove();//~~
         $( elem ).fadeIn("slow");
         $( elem ).css({ 
            position : 'absolute',
            left : ($(window).width() - $( elem ).outerWidth())/2,
            top : ($(window).height() - $( elem ).outerHeight())/2 + $(document).scrollTop()
         });
      }
      /* popDialog : function ( num, price ) {
         var elem = $("#dialogS");
         $("#tipTotalN").innerHTML = num;
         $("#tipTotalP").innerHTML = price;
         $( elem ).fadeIn("slow");
         $( elem ).css({ 
            position : 'absolute',
            left : ($(window).width() - $( elem ).outerWidth())/2,
            top : ($(window).height() - $( elem ).outerHeight())/2
         });
      } */
   };
   validator = {
      nickReg : /^[\w_]{4,16}$/,
      emailReg : /\w+([-+.]\w+)*@\w+([-.]\w+)*\.\w+([-.]\w+)*/,
      mobileReg : /^1{1}\d{10}$/,
      passReg : /^.{6,16}$/,
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
      checkEmail : function ( inputNode, tipNode ) {
         var t = this;
         if( $( inputNode ).text() == "" ) {
            $( tipNode ).html("邮箱地址不能为空");
         }

         if( !t.checkVal( t.emailReg, inputNode, tipNode ) ) {
            $( tipNode ).html("邮箱地址格式不正确");
            return false;
         } else {
            return true;
         };
         //t.checkVal( t.emailReg, $( t.emailNode ), $( t.emailTipNode ));
      },
      // 验证密码格式
      checkPass : function ( inputNode, tipNode ) {
         var t = this;
         if( $( inputNode ).text() == "" ) {
            $( tipNode ).html("密码不能为空");
         }
         if( !t.checkVal( t.passReg, inputNode, tipNode ) ) {
            $( tipNode ).html("密码格式错误，长度应为：6-16位");
            return false;
         } else {
            return true;
         };
         //t.checkVal( t.passReg, $( t.pass1Node ), $( t.pass1TipNode ));
      },
      // 验证两次密码输入
      checkPass2 : function ( pass1Node, pass2Node, pass2TipNode ) {
         var t = this;
         if ( pass1Node.text() != pass2Node.text() ) {
            pass2TipNode.fadeIn("slow");
            return false;
         } else {
            pass2TipNode.fadeOut("slow");
            return true;
         };
      },
      // 验证昵称
      checkNick : function ( inputNode, tipNode ) {
         var t = this;
         return;
         if( $( inputNode ).text() == "" ) {
            $( tipNode ).html("昵称不能为空");
         }
         if( !t.checkVal( t.passReg, inputNode, tipNode ) ) {
            $( tipNode ).html("昵称格式不正确，只能是字母、数字或下划线");
            return false;
         } else {
            return true;
         };
      },
      checkMobile : function ( inputNode, tipNode ) {
         var t = this;
         if( $( inputNode ).text() != "" && !t.checkVal( t.mobileReg, inputNode, tipNode ) ) {
            $( tipNode ).html("手机格式不正确");
            return false;
         } else {
            return true;
         }
      }
   };
   city = {"province":["省/直辖市", "安徽", "北京", "重庆", "福建", "甘肃", "广东", "广西", "贵州", "海南", "河北", "黑龙江", "河南", "湖北", "湖南", "内蒙古", "江苏", "江西", "吉林", "辽宁", "宁夏", "青海", "山西", "山东", "上海", "四川", "天津", "西藏", "新疆", "云南", "浙江", "陕西", "台湾", "香港", "澳门", "海外", "其他"],"city":["城市/地区","合肥,芜湖,蚌埠,淮南,马鞍山,淮北,铜陵,安庆,黄山,滁州,阜阳,宿州,巢湖,六安,亳州,池州,宣城","东城区,西城区,崇文区,宣武区,朝阳区,丰台区,石景山区,海淀区,门头沟区,房山区,通州区,顺义区,昌平区,大兴区,怀柔区,平谷区,密云县,延庆县","万州区,涪陵区,渝中区,大渡口区,江北区,沙坪坝区,九龙坡区,南岸区,北碚区,万盛区,双桥区,渝北区,巴南区,黔江区,长寿区,綦江县,潼南县,铜梁县,大足县,荣昌县,璧山县,梁平县,城口县,丰都县,垫江县,武隆县,忠县,开县,云阳县,奉节县,巫山县,巫溪县,石柱土家族自治县,秀山土家族苗族自治县,酉阳土家族苗族自治县,彭水苗族土家族自治县,江津市,合川市,永川区,南川市","福州,厦门,莆田,三明,泉州,漳州,南平,龙岩,宁德","兰州,嘉峪关,金昌,白银,天水,武威,张掖,平凉,酒泉,庆阳,定西,陇南,临夏,甘南","广州,韶关,深圳,珠海,汕头,佛山,江门,湛江,茂名,肇庆,惠州,梅州,汕尾,河源,阳江,清远,东莞,中山,潮州,揭阳,云浮","南宁,柳州,桂林,梧州,北海,防城港,钦州,贵港,玉林,百色,贺州,河池","贵阳,六盘水,遵义,安顺,铜仁,黔西南,毕节,黔东南,黔南","海口,三亚,其他","石家庄,唐山,秦皇岛,邯郸,邢台,保定,张家口,承德,沧州,廊坊,衡水","哈尔滨,齐齐哈尔,鸡西,鹤岗,双鸭山,大庆,伊春,佳木斯,七台河,牡丹江,黑河,绥化,大兴安岭","郑州,开封,洛阳,平顶山,安阳,鹤壁,新乡,焦作,濮阳,许昌,漯河,三门峡,南阳,商丘,信阳,周口,驻马店","武汉,黄石,十堰,宜昌,襄樊,鄂州,荆门,孝感,荆州,黄冈,咸宁,随州,恩施土家族苗族自治州","长沙,株洲,湘潭,衡阳,邵阳,岳阳,常德,张家界,益阳,郴州,永州,怀化,娄底,湘西土家族苗族自治州","呼和浩特,包头,乌海,赤峰,通辽,鄂尔多斯,呼伦贝尔,兴安盟,锡林郭勒盟,乌兰察布盟,巴彦淖尔盟,阿拉善盟","南京,无锡,徐州,常州,苏州,南通,连云港,淮安,盐城,扬州,镇江,泰州,宿迁","南昌,景德镇,萍乡,九江,新余,鹰潭,赣州,吉安,宜春,抚州,上饶","长春,吉林,四平,辽源,通化,白山,松原,白城,延边朝鲜族自治州","沈阳,大连,鞍山,抚顺,本溪,丹东,锦州,营口,阜新,辽阳,盘锦,铁岭,朝阳,葫芦岛","银川,石嘴山,吴忠,固原","西宁,海东,海北,黄南,海南,果洛,玉树,海西","太原,大同,阳泉,长治,晋城,朔州,晋中,运城,忻州,临汾,吕梁","济南,青岛,淄博,枣庄,东营,烟台,潍坊,济宁,泰安,威海,日照,莱芜,临沂,德州,聊城,滨州,菏泽","黄浦区,卢湾区,徐汇区,长宁区,静安区,普陀区,闸北区,虹口区,杨浦区,闵行区,宝山区,嘉定区,浦东新区,金山区,松江区,青浦区,南汇区,奉贤区,崇明县","成都,自贡,攀枝花,泸州,德阳,绵阳,广元,遂宁,内江,乐山,南充,眉山,宜宾,广安,达州,雅安,巴中,资阳,阿坝,甘孜,凉山","和平区,河东区,河西区,南开区,河北区,红桥区,塘沽区,汉沽区,大港区,东丽区,西青区,津南区,北辰区,武清区,宝坻区,宁河县,静海县,蓟县","拉萨,昌都,山南,日喀则,那曲,阿里,林芝","乌鲁木齐,克拉玛依,吐鲁番,哈密,昌吉,博尔塔拉,巴音郭楞,阿克苏,克孜勒苏,喀什,和田,伊犁,塔城,阿勒泰,石河子","昆明,曲靖,玉溪,保山,昭通,楚雄,红河,文山,思茅,西双版纳,大理,德宏,丽江,怒江,迪庆,临沧","杭州,宁波,温州,嘉兴,湖州,绍兴,金华,衢州,舟山,台州,丽水","西安,铜川,宝鸡,咸阳,渭南,延安,汉中,榆林,安康,商洛","台北,高雄,其他","香港","澳门","美国,英国,法国,俄罗斯,加拿大,巴西,澳大利亚,印尼,泰国,马来西亚,新加坡,菲律宾,越南,印度,日本,其他",'其他']}
   userInfo = {
      init : function () {
         var t = this;
         t.nickNode = $("#nickname");
         t.emailNode = $("#email");
         t.mobileNode = $("#mobile");
         t.ProvinceNode = $("#Province");
         t.cityNode = $("#city");
         t.oldPassNode = $("#oldPass");
         t.newPassNode = $("#newPass");
         t.newPass2Node = $("#newPass2");
         
         t.nickTipNode = $("#nickTip");
         t.emailTipNode = $("#emailTip");
         t.mobileTipNode = $("#mobileTip");
         //t.cityTipNode = $("#city + td");
         t.oldPassTipNode = $("#oldPassTip");
         t.newPassTipNode = $("#newPassTip");
         t.newPass2TipNode = $("#newPass2Tip");
         
         t.changeInfoBtn = $("#changeInfoBtn");
         t.changePassBtn = $("#changePassBtn");
         try {
            $("#city").json2select(areaJson);
         } catch ( err ) {
         }
         $( t.nickNode ).bind("blur", function () {
            t.checkNick();
         });
         $( t.nickNode ).bind("focus", function () {
            $( t.nickTipNode ).fadeOut("slow");
         });
         $( t.emailNode ).bind("blur", function () {
            t.checkEmail();
         });
         $( t.emailNode ).bind("focus", function () {
            $( t.emailTipNode ).fadeOut("slow");
         });
         $( t.mobileNode ).bind("blur", function () {
            t.checkMobile();
         });
         $( t.mobileNode ).bind("focus", function () {
            $( t.mobileTipNode ).fadeOut("slow");
         });
         $( t.oldPassNode ).bind("blur", function () {
            t.checkOldPass();
         });
         $( t.oldPassNode ).bind("focus", function () {
            $( t.oldPassTipNode ).fadeOut("slow");
         });
         $( t.newPassNode ).bind("blur", function () {
            t.checkNewPass();
         });
         $( t.newPassNode ).bind("focus", function () {
            $( t.newPassTipNode ).fadeOut("slow");
         });
         $( t.newPassNode2 ).bind("blur", function () {
            t.checkNewPass2();
         });
         $( t.newPassNode2 ).bind("focus", function () {
            $( t.newPass2TipNode ).fadeOut("slow");
         });
         
         $( t.ProvinceNode ).bind("click", function () {
            
         });
         $( t.changeInfoBtn ).bind("click", function () {
            t.checkInfo();
         });
         $( t.changePassBtn ).bind("click", function () {
            t.checkPassInfo();
         });
         t.renderCity();
      },
      // 加载省份
      renderCity : function () {
         var t = this,
             i = 0,
             str = '';
         for ( ; i < city.province.length; i ++ ) {
            str += "<option value='" + i + "'>" + city.province[i] + "</option>";
         }
         t.ProvinceNode.html( str );
         t.cityNode.html( "<option>" + city.city[0] + "</option>" );
      },
      // 加载城市信息
      loadCity : function ( node ) {
         var t = this
             str = ''
             i = 0
             id = Number( node.value ),
             currentCity = city.city[ id ].split(',');
         for ( ; i < currentCity.length; i ++ ) {
            str += "<option>" + currentCity[i] + "</option>";
         }
         t.cityNode.html( str );
      },
      // 验证邮件格式
      checkEmail : function () {
         var t = this;
         validator.checkEmail( t.emailNode, t.emailTipNode );
      },
      // 验证旧密码格式
      checkOldPass : function () {
         var t = this;
         validator.checkPass( t.oldPassNode, t.oldPassTipNode );
      },
      // 验证新密码格式
      checkNewPass : function () {
         var t = this;
         validator.checkPass( t.newPassNode, t.newPassTipNode );
      },
      // 验证两次新密码输入
      checkNewPass2 : function () {
         var t = this;
         validator.checkPass2( t.newPassNode, t.newPass2Node, t.newPass2TipNode );
      },
      // 验证昵称
      checkNick : function () {
         var t = this;
         validator.checkNick( t.nickNode, t.nickTipNode );
      },
      // 验证手机
      checkMobile : function () {
         var t = this;
         validator.checkMobile( t.mobileNode, t.mobileTipNode );
      },
      checkInfo : function () {
         var t = this;
         if ( t.checkEmail && t.checkMobile() && t.checkNick() ) {
            document.info.submit();
         };
      },
      checkPassInfo : function () {
         var t = this;
         if ( t.checkOldPass && t.checkNewPass() && t.checkNewPass2() ) {
            document.pass.submit();
         };
      }
   };
   reg = {
      // 初始化
      init : function () {
         var t = this;
         t.emailNode = $("#email");
         t.pass1Node = $("#pass1");
         t.pass2Node = $("#pass2");
         t.nicknameNode = $("#nickname");
         t.emailTipNode = $("#emailTip");
         t.pass1TipNode = $("#pass1Tip");
         t.pass2TipNode = $("#pass2Tip");
         t.nicknameTipNode = $("#nicknameTip");
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
      // 验证邮件格式
      checkEmail : function () {
         var t = this;
         validator.checkEmail( t.emailNode, t.emailTipNode );
      },
      // 验证密码格式
      checkPass : function () {
         var t = this;
         validator.checkVal( t.pass1Node, t.pass1TipNode );
      },
      // 验证两次密码输入
      checkPass2 : function () {
         var t = this;
         validator.checkPass2( t.pass1Node, t.pass1Node, t.pass2TipNode );
      },
      // 验证昵称
      checkNick : function () {
         var t = this;
         validator.checkNick( t.nicknameNode, t.nicknameTipNode );
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