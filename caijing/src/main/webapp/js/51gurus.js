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
      // 产品集合
      pObj : {
         p1:{id:1, title:"分析师成功率", intro:"套餐", price:29, byIndustry:true},
         p2:{id:2, title:"分析师收益率", intro:"套餐", price:29, byIndustry:true},
         p3:{id:3, title:"个股追踪", intro:"套餐", price:29, byIndustry:false},
         p4:{id:4, title:"推荐后折价", intro:"套餐", price:19, byIndustry:false},
         p5:{id:5, title:"研报速递", intro:"套餐", price:99, byIndustry:true},
         p6:{id:6, title:"民间专家直播室", intro:"套餐", price:99, byIndustry:false},
         p7:{id:7, title:"公告掘金", intro:"套餐", price:19, byIndustry:false},
         p8:{id:8, title:"年报查询", intro:"套餐", price:29, byIndustry:false},//~~~~~
         p9:{id:9, title:"分析师成功率+研报速递(行业)", intro:"套餐", price:109, byIndustry:true},
         p10:{id:10, title:"分析师收益率+研报速递(行业)", intro:"套餐",price:109, byIndustry:true},
         p11:{id:11, title:"研报速递+分析师成功率+分析师收益率(行业)", intro:"套餐",price:129, byIndustry:true},
         p12:{id:12, title:"推荐后折价（年）", intro:"套餐", price:190, byIndustry:false},
         p13:{id:13, title:"个股追踪 （年）", intro:"套餐", price:290, byIndustry:false},
         p14:{id:14, title:"公告掘金 （年）", intro:"套餐", price:190, byIndustry:false},
         p15:{id:15, title:"年报查询 （年）", intro:"套餐", price:290, byIndustry:false}
      },
      // 行业列表
      industry : {
         i1: {id: 1, name: "房地产业"},
         i2: {id: 2, name: "银行业"},
         i3: {id: 3, name: "非银行金融"},
         i4: {id: 4, name: "电力煤气及水等公用事业"},
         i5: {id: 5, name: "有色金属"},
         i6: {id: 6, name: "基础化工"},
         i7: {id: 7, name: "机械设备"},
         i8: {id: 8, name: "医药生物"},
         i9: {id: 9, name: "汽车和汽车零部件"},
         i10: {id: 10, name: "煤炭开采"},
         i11: {id: 11, name: "批发和零售贸易"},
         i12: {id: 12, name: "建筑和工程"},
         i13: {id: 13, name: "家电行业"},
         i14: {id: 14, name: "电子行业"},
         i15: {id: 15, name: "纺织和服装"},
         i16: {id: 16, name: "造纸印刷业"},
         i17: {id: 17, name: "食品饮料业"},
         i18: {id: 18, name: "社会服务业"},
         i19: {id: 19, name: "传播与文化"},
         i20: {id: 20, name: "农林牧渔"},
         i21: {id: 21, name: "计算机"},
         i22: {id: 22, name: "通信行业"},
         i23: {id: 23, name: "新能源"},
         i24: {id: 24, name: "钢铁行业"},
         i25: {id: 25, name: "石油化工"},
         i26: {id: 26, name: "交通运输仓储"},
         i27: {id: 27, name: "非金属类建材"},
         i28: {id: 28, name: "采掘业"},
         i29: {id: 29, name: "信息技术业"},
         i30: {id: 30, name: "其他制造业"}
      },
      // 初始化购物车
      /* init : function ( orderId ) {
         var t = this;
         t.orderId = orderId;
         t.cartArr = [];
         t.initIndustrySelect();
         // t.buyBind();
         Rookie(function(){
            if ( !this.read("orderId") ) {
               t.orderId = +new Date();
               this.write( 'orderId', t.orderId );
            } else if ( t.orderId == this.read("orderId") ) {
               //alert( "应该清空本地数据" );
               t.clear();
            } else {
               var cartItem = this.read('cart');
               if( cartItem ) {
                  t.cartArr = this.read('cart');
               }
            }
         })
      }, */
      init : function () {
         var t = this;
         t.cartArr = [];
         t.initIndustrySelect();
         // t.buyBind();
         Rookie(function(){
            var cartItem = this.read('cart');
            if( cartItem ) {
               t.cartArr = this.read('cart');
            }
         })
      },
      buyBind : function () {
         $("#0num").bind("change", function () {
            var industryId = $("#dialogS .chose select").val();
            alert( industryId );
         });
      },
      initIndustrySelect : function () {
         var t = this;
         for ( n in t.industry) {
            $("#dialogS .chose select").append("<option value='" + t.industry[n].id + "'>" + t.industry[n].name + "</option>");
         }
      },
      // 购买
      buy : function ( id, num, jump ) {
         var t = this;
         var id = "p" + id;
         t.jump = jump;
         
         t.popDialog();
         $("#dialogS .chose").show();
         $("#dialogS .success").hide();
         $("#chosePrice").text( t.pObj[id].price );
         /* $("#buyOk").bind("click", function () {
            getTotal( id );
         }); */
         document.getElementById("buyOk").onclick = function () {
            getTotal( id );
         }
         $("#0num").bind("blur", function () {
            var price = t.pObj[id].price * $("#0num").val();
            $("#chosePrice").text( price );
         });
         if ( t.pObj[id].byIndustry ) {
            $("#dialogS .chose .industcol").show();
            $("#dialogS .chose select").show();
         } else {
            $("#dialogS .chose .industcol").hide();
            $("#dialogS .chose select").hide();
         }
         function getTotal ( id ) {
            $("#dialogS").fadeOut();
            var num = $("#0num").val(),
                industryId = $("#dialogS .chose select").val();
            if ( !t.pObj[id].byIndustry ) {
               industryId = "";
            }
            var isOk = t.add( id, num, industryId );
            if ( !isOk ) {
               alert("不能重复添加");
               return;
            }
            
            if ( jump ) {
               window.location.href = "/cart/myCart.htm";
            }
         }
         return;
      },
      // 清除购物车
      clear : function () {
         var t = this;
         Rookie(function(){
            this.clear("cart");
         });
         window.location.reload();
      },
      // 添加一个产品
      add : function ( id, num, industryId ) {
         var t = this,
         isExist = false;
         var price = t.pObj[id].price;
         var total = { num : 0, price : 0 };
         for ( var i = 0; i < t.cartArr.length; i ++ ) {
            total.num += Number(t.cartArr[i].num);
            total.price += t.cartArr[i].num * t.cartArr[i].price;
            if ( id == t.cartArr[i].id && industryId == t.cartArr[i].industryId ) {
               isExist = true;
            }
         };
         if( isExist ) {
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
         t.popDialog( total.num, total.price );
         $("#tipTotalN").innerHTML = num;
         $("#tipTotalP").innerHTML = price;
         if ( !t.jump ) {
            $("#dialogS .chose").hide();
            $("#dialogS .success").show();
         }
            
         if ( $("#cartTb") ) {
            // console.log("obj is:" + t.pObj + "," + id);
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
         return true;
      },
      // 删除某个产品
      del : function ( id ) {
         var yes = confirm("是否要删除该产品？");
         if ( !yes ) {
            return;
         }
         var id = "p" + id;
         for ( var i = 0; i < t.cartArr.length; i ++ ) {
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
         var t = this;
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
             str = "",
             pid = "",
             industryId = "";
         Rookie(function(){
            //console.log("dsfsdfdsf" + this.read('cart'));
            t.cartArr = this.read('cart');
            if ( !t.cartArr ) {
               return;
            }
            showit( pay );
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
         });
         function showit ( pay ) {
            for ( var i = 0; i < t.cartArr.length; i++ ) {
               pid = t.cartArr[i].id;
               num = t.cartArr[i].num;
               if ( t.pObj[pid] ) {
                  price = t.pObj[pid].price * num;
                  totalN += num;
                  totalP += price;
                  var str = '<tr id="' + pid.split("p")[1] + '">';
                  str += '<td>' + pid.split("p")[1] + '</td>';
                  str += '<td>' + t.pObj[pid].title + '</td>';
                  str += '<td>' + t.pObj[pid].intro + '</td>';
                  if ( pay ) {
                     str += '<td><span id="' + pid + 'num">' + num + '</span></td>';
                  } else {
                     str += '<td><span class="operation" onclick="cart.sub(\'' + pid + '\')">-</span><span id="' + pid + 'num">' + num + '</span><span class="operation" onclick="cart.plus(\'' + pid + '\')">+</span></td>';
                  }
                  str += '<td><span class="price">' + price + '</span>元</td>';
                  str += '<td><a onclick="cart.del(' + pid + ')" href="javascript:;">删除</a></td>';
                  str += '</tr>';
                  $( cartTb ).append('\
                     <tr id="' + pid.split("p")[1] + '">\
                        <td>' + pid.split("p")[1] + '</td>\
                        <td>' + t.pObj[pid].title + '</td>\
                        <td>' + t.pObj[pid].intro + '</td>\
                        <td><span class="operation" onclick="cart.sub(\'' + pid + '\')">-</span><span id="' + pid + 'num">' + num + '</span><span class="operation" onclick="cart.plus(\'' + pid + '\')">+</span></td>\
                        <td><span class="price">' + price + '</span>元</td>\
                        <td><a onclick="cart.del(' + pid + ')" href="javascript:;">删除</a></td>\
                     </tr>\
                  ');
               }
            };
            $("#totalN").text( totalN );
            $("#totalP").text( totalP );
         }
      },
      // 弹出提示框
      popDialog : function () {
         var elem = $("#dialogS");
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
   userInfo = {
      init : function () {
         var t = this;
         t.nickNode = $("#nickname");
         t.emailNode = $("#email");
         t.mobileNode = $("#mobile");
         //t.ProvinceNode = $("#Province");
         //t.cityNode = $("#city");
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