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
                  Highcharts.dateFormat('%Y��%m��%d��', this.x) +': '+ this.y +'%';
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
               type:"datetime",//ʱ����Ҫ�������type��Ĭ����linear
               maxPadding : 0.05,
               minPadding : 0.05,
               //tickInterval : 24 * 3600 * 1000 * 2,//���컭һ��x�̶�
               //����150px��һ��x�̶ȣ�����������Ǹ�һ�������ˣ��������ļ��Ϊ׼
               tickPixelInterval : 60,
               tickWidth:1,//�̶ȵĿ��
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
         elem.innerHTML = "<div id='analyHolder'></div><div class='close'><a href='javascript:;' onclick='hide(\"analyLayer\")'>�ر�</a></div>";
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
      // ��ʼ�����ﳵ
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
               alert( "Ӧ����ձ�������" );
               t.clear();
            } else {
               var cartItem = this.read('cart');
               if( cartItem ) {
                  t.cartArr = this.read('cart');
               }
            }
         })
      },
      buy : function ( id, num, price ) {
         var t = this;
         Rookie(function(){
            t.orderId = +new Date();
            this.write( 'orderId', t.orderId );
            t.add( id, num, price );
            window.location.href = "";
         })
      },
      // ������ﳵ
      clear : function () {
         var t = this;
         Rookie(function(){
            this.clear("cart");
            this.clear("orderId");
         });
      },
      /* 
      // ���һ����Ʒ
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
            alert("�����ظ����");
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
      // ���һ����Ʒ
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
            alert("�����ظ����");
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
                  <td><span class="price">' + price + '</span>Ԫ</td>\
                  <td><a onclick="cart.del(' + id + ')" href="javascript:;">ɾ��</a></td>\
               </tr>\
            ');
         }
      },
      // ɾ��ĳ����Ʒ
      del : function ( id ) {
         for ( var i = 0; i < t.cartArr; i ++ ) {
            if ( id == t.cartArr[i].id ) {
               t.cartArr.splice( i, 1 );
            }
         };
         $( "#" + id ).remove(); // ɾ���ýڵ�
         Rookie(function(){
            this.write( 'cart', t.cartArr );
         });
      },
      // ��Ʒ������һ
      plus : function ( id ) {
         var t = this,
             num = Number($( "#" + id + "num" ).text());
         num += 1;
         $( "#" + id + "num" ).text( num );
         t.motify( id, "num", num );
      },
      // ��Ʒ������һ
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
      // �޸�����
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
      // չʾ����
      show : function () {
         /**
          * pObj: {0002:{id:0002,title:"����ʦ�ɹ���",intro:"�ײ�",price:10},0003:{...}}
          */
         var t = this,
             cartTb = $("#cartTb"),
             pid = "", // ��Ʒid
             num = 1, // ��Ʒ����
             price = 0, // �۸�
             totalN = 0, // ������
             totalP = 0; // �ܼ۸�
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
                  <td><span class="price">' + price + '</span>Ԫ</td>\
                  <td><a onclick="cart.del(' + pid + ')" href="javascript:;">ɾ��</a></td>\
               </tr>\
            ');
         };
         $("#totalN").text( totalN );
         $("#totalP").text( totalP );
      },
      // ������ʾ��
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
      // ��֤�ʼ���ʽ
      checkEmail : function ( inputNode, tipNode ) {
         var t = this;
         if( $( inputNode ).text() == "" ) {
            $( tipNode ).html("�����ַ����Ϊ��");
         }
         if( !t.checkVal( t.emailReg, inputNode, tipNode ) ) {
            $( tipNode ).html("�����ַ��ʽ����ȷ");
            return false;
         } else {
            return true;
         };
         //t.checkVal( t.emailReg, $( t.emailNode ), $( t.emailTipNode ));
      },
      // ��֤�����ʽ
      checkPass : function ( inputNode, tipNode ) {
         var t = this;
         if( $( inputNode ).text() == "" ) {
            $( tipNode ).html("���벻��Ϊ��");
         }
         if( !t.checkVal( t.passReg, inputNode, tipNode ) ) {
            $( tipNode ).html("�����ʽ���󣬳���ӦΪ��6-16λ");
            return false;
         } else {
            return true;
         };
         //t.checkVal( t.passReg, $( t.pass1Node ), $( t.pass1TipNode ));
      },
      // ��֤������������
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
      // ��֤�ǳ�
      checkNick : function ( inputNode, tipNode ) {
         var t = this;
         if( $( inputNode ).text() == "" ) {
            $( tipNode ).html("�ǳƲ���Ϊ��");
         }
         if( !t.checkVal( t.passReg, inputNode, tipNode ) ) {
            $( tipNode ).html("�ǳƸ�ʽ����ȷ��ֻ������ĸ�����ֻ��»���");
            return false;
         } else {
            return true;
         };
      },
      checkMobile : function ( inputNode, tipNode ) {
         var t = this;
         if( $( inputNode ).text() != "" && !t.checkVal( t.mobileReg, inputNode, tipNode ) ) {
            $( tipNode ).html("�ֻ���ʽ����ȷ");
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
            if( typeof console != "undefined" ) {
               console.log( err );
            }
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
      // ��֤�ʼ���ʽ
      checkEmail : function () {
         validator.checkEmail( t.emailNode, t.emailTipNode );
      },
      // ��֤�������ʽ
      checkOldPass : function () {
         validator.checkPass( t.oldPassNode, t.oldPassTipNode );
      },
      // ��֤�������ʽ
      checkNewPass : function () {
         validator.checkPass( t.newPassNode, t.newPassTipNode );
      },
      // ��֤��������������
      checkNewPass2 : function () {
         validator.checkPass2( t.newPassNode, t.newPass2Node, t.pass2TipNode );
      },
      // ��֤�ǳ�
      checkNick : function () {
         validator.checkNick( t.nicknameNode, t.nicknameTipNode );
      },
      // ��֤�ֻ�
      checkMobile : function () {
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
      // ��ʼ��
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
         // ��email��֤�¼�
         $( t.emailNode ).bind("blur", function () {
            t.checkEmail();
         });
         $( t.emailNode ).bind("focus", function () {
            $( t.emailTipNode ).fadeOut("slow");
         });
         // ��������֤�¼�
         $( t.pass1Node ).bind("blur", function () {
            t.checkPass();
         });
         $( t.pass1Node ).bind("focus", function () {
            $( t.pass1TipNode ).fadeOut("slow");
         });
         // ��������֤�¼�
         $( t.pass2Node ).bind("blur", function () {
            t.checkPass2();
         });
         $( t.pass2Node ).bind("focus", function () {
            $( t.pass2TipNode ).fadeOut("slow");
         });
         // ���ǳ���֤�¼�
         $( t.nicknameNode ).bind("blur", function () {
            t.checkNick();
         });
         $( t.nicknameNode ).bind("focus", function () {
            $( t.nicknameTipNode ).fadeOut("slow");
         });
      },
      // ��֤�ʼ���ʽ
      checkEmail : function () {
         validator.checkEmail( t.emailNode, t.emailTipNode );
      },
      // ��֤�����ʽ
      checkPass : function () {
         validator.checkVal( t.pass1Node, t.pass1TipNode );
      },
      // ��֤������������
      checkPass2 : function () {
         validator.checkPass2( t.pass1Node, t.pass1Node, t.pass2TipNode );
      },
      // ��֤�ǳ�
      checkNick : function () {
         validator.checkPass2( t.nicknameNode, t.nicknameTipNode );
      },
      // ��֤�����ύ
      formSubmit : function () {
         var t = this;
         if ( t.checkEmail && t.checkPass() &&t.checkPass2() && t.checkNick() ) {
            document.reg.submit();
         };
      }
   };
})(window);