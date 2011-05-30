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
      // ��Ʒ����
      /* pObj : {
         p1:{id:1, title:"����ʦ�ɹ���", intro:"�ײ�", price:29, byIndustry:true},
         p2:{id:2, title:"����ʦ������", intro:"�ײ�", price:29, byIndustry:true},
         p3:{id:3, title:"����׷��", intro:"�ײ�", price:29, byIndustry:false},
         p4:{id:4, title:"�Ƽ����ۼ�", intro:"�ײ�", price:19, byIndustry:false},
         p5:{id:5, title:"�б��ٵ�", intro:"�ײ�", price:99, byIndustry:true},
         p6:{id:6, title:"���ר��ֱ����", intro:"�ײ�", price:99, byIndustry:false},
         p7:{id:7, title:"������", intro:"�ײ�", price:19, byIndustry:false},
         p8:{id:8, title:"�걨��ѯ", intro:"�ײ�", price:29, byIndustry:false},//~~~~~
         p9:{id:9, title:"����ʦ�ɹ���+�б��ٵ�(��ҵ)", intro:"�ײ�", price:109, byIndustry:true},
         p10:{id:10, title:"����ʦ������+�б��ٵ�(��ҵ)", intro:"�ײ�",price:109, byIndustry:true},
         p11:{id:11, title:"�б��ٵ�+����ʦ�ɹ���+����ʦ������(��ҵ)", intro:"�ײ�",price:129, byIndustry:true},
         p12:{id:12, title:"�Ƽ����ۼۣ��꣩", intro:"�ײ�", price:190, byIndustry:false},
         p13:{id:13, title:"����׷�� ���꣩", intro:"�ײ�", price:290, byIndustry:false},
         p14:{id:14, title:"������ ���꣩", intro:"�ײ�", price:190, byIndustry:false},
         p15:{id:15, title:"�걨��ѯ ���꣩", intro:"�ײ�", price:290, byIndustry:false}
      }, */
      // ��ҵ�б�
      /* industry : {
         i1: {id: 1, name: "���ز�ҵ"},
         i2: {id: 2, name: "����ҵ"},
         i3: {id: 3, name: "�����н���"},
         i4: {id: 4, name: "����ú����ˮ�ȹ�����ҵ"},
         i5: {id: 5, name: "��ɫ����"},
         i6: {id: 6, name: "��������"},
         i7: {id: 7, name: "��е�豸"},
         i8: {id: 8, name: "ҽҩ����"},
         i9: {id: 9, name: "�����������㲿��"},
         i10: {id: 10, name: "ú̿����"},
         i11: {id: 11, name: "����������ó��"},
         i12: {id: 12, name: "�����͹���"},
         i13: {id: 13, name: "�ҵ���ҵ"},
         i14: {id: 14, name: "������ҵ"},
         i15: {id: 15, name: "��֯�ͷ�װ"},
         i16: {id: 16, name: "��ֽӡˢҵ"},
         i17: {id: 17, name: "ʳƷ����ҵ"},
         i18: {id: 18, name: "������ҵ"},
         i19: {id: 19, name: "�������Ļ�"},
         i20: {id: 20, name: "ũ������"},
         i21: {id: 21, name: "�����"},
         i22: {id: 22, name: "ͨ����ҵ"},
         i23: {id: 23, name: "����Դ"},
         i24: {id: 24, name: "������ҵ"},
         i25: {id: 25, name: "ʯ�ͻ���"},
         i26: {id: 26, name: "��ͨ����ִ�"},
         i27: {id: 27, name: "�ǽ����ཨ��"},
         i28: {id: 28, name: "�ɾ�ҵ"},
         i29: {id: 29, name: "��Ϣ����ҵ"},
         i30: {id: 30, name: "��������ҵ"}
      }, */
      pObj : {},
      recommend : [],
      industry : {},
      // ��ʼ�����ﳵ
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
               //alert( "Ӧ����ձ�������" );
               t.clear();
            } else {
               var cartItem = this.read('cart');
               if( cartItem ) {
                  t.cartArr = this.read('cart');
               }
            }
         })
      }, */
      init : function ( action ) {
         var t = this;
         t.cartArr = [];
         t.getIndustry();
         t.getMaster();
         t.getProducts( action );
         
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
      getProducts : function ( action ) {
         var t= this;
         $.get('/get/product.do', function(data) {
            var products = eval('(' + data + ')');
            var i = 1;
            for ( n in products ) {
               if ( products[n].name.indexOf("�ݸ�") ) {
                  t.pObj["p" + products[n].pid] = products[n];
               }
               i += 1;
            }
            if ( action == "showProducts") {
               t.drawProductsTable( t.pObj );
            }
         });
      },
      drawProductsTable : function ( obj ) {
         var t = this,
             industryStr = "",
             monthStr = "",
             tbStr = "<table width='99%' class='productsTable' cellspacing='0' cellpadding='0' border='1'>";
             tbStr += "<tr><th width='120'>��Ʒ</th><th>��Ʒ˵��</th><th>����۸�(�����)</th><th width='70'>����</th></tr>";
         //for ( var i = 0; i < obj.length; i ++ ) {
         for ( n in obj ) {
            if ( !obj[n].name.indexOf("�ݸ�") ) {
               return;
            }
            if ( obj[n].intro ) {
               t.recommend.push( obj[n] );
               continue;
            }
            tbStr += "<tr id='product" + obj[n].pid + "'>";
            tbStr += "<td>" + obj[n].name + "</td>";
            tbStr += "<td>" + obj[n].description + "</td>";
            if ( obj[n].isIndustry ) {
               industryStr = "/��ҵ";
            } else {
               industryStr = "";
            }
            if ( obj[n].continuedmonth ) {
               monthStr = "��";
            } else {
               monthStr = "��";
            }
            tbStr += "<td>" + obj[n].price + "Ԫ/" + monthStr + industryStr + "</td>";
            tbStr += "<td><a class='cRed' href='javascript:;' onclick='cart.buy(" + obj[n].pid + ")'>���빺�ﳵ</a></td>";
            tbStr += "</tr>";
         }
         tbStr += "</table>";
         $("#products").html( tbStr );
      },
      getIndustry : function () {
         var t= this;
         $.get('/get/industry.do', function(data) {
            var industry = eval('(' + data + ')');
            var i = 1;
            for ( n in industry ) {
               // t.industry["i" + i] = industry[n];
               t.industry[industry[n].industryid] = industry[n];
               i += 1;
            }
            t.initIndustrySelect( t.industry );
         });
      },
      getMaster : function () {
         var t = this;
         $.get('/get/master.do', function(data) {
            t.masterArr = eval( '(' + data + ')' );
            var tbStr = "<p class='cart-live-p'>�ݸ���ʦֱ����:</p>";
            tbStr += "<table width='99%' class='productsTable' cellspacing='0' cellpadding='0' border='1'>";
            tbStr += "<tr><th width='100'>��ʦ</th><th>��ʦ����</th><th width='70'>����</th></tr>";
            for ( var i = 0; i < t.masterArr.length; i ++ ) {
               tbStr += "<tr>";
               tbStr += "<td>" + t.masterArr[i].mastername + "</td>";
               tbStr += "<td>" + t.masterArr[i].intro + "</td>";
               tbStr += "<td><a class='cRed' href='javascript:;' onclick='cart.buyLive(10, " + t.masterArr[i].masterid + " )'>���빺�ﳵ</a></td>";
               tbStr += "</tr>";
            }
            tbStr += "</table>";
            $("#products").append( tbStr );
         });
      },
      /* buyBind : function () {
         $("#0num").bind("change", function () {
            var industryId = $("#dialogS .chose select").val();
            alert( industryId );
         });
      }, */
      initIndustrySelect : function ( industry ) {
         var t = this;
         //console.log( industry );
         for ( n in industry) {
            $("#dialogS .chose select").append("<option value='" + industry[n].industryid + "'>" + industry[n].industryname + "</option>");
         }
      },
      // ����
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
            var price = t.pObj[id].price * $("#0num").val();
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
            var num = $("#0num").val(),
                industryId = $("#dialogS .chose select").val();
            if ( !t.pObj[id].isIndustry ) {
               industryId = "";
            }
            t.add( id, num, industryId );
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
      // ������ﳵ
      clear : function () {
         var t = this;
         Rookie(function(){
            this.clear("cart");
         });
         window.location.reload();
      },
      // ���һ����Ʒ
      add : function ( id, num, industryId ) {
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
            alert("�����ظ����");
            return false;
         };
         total.num += num;
         total.price += num * price;
         t.cartArr.push({'id' : id, 'num' : num, 'industryId' : industryId});
         //console.log(t.cartArr);
         Rookie(function(){
            this.write( 'cart', t.cartArr );
         });
         // ������ʾ
         
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
                  <td><span class="price">' + price + '</span>Ԫ</td>\
                  <td><a onclick="cart.del(\'' + id + '\')" href="javascript:;">ɾ��</a></td>\
               </tr>\
            ');
         }
         return true;
      },
      // ɾ��ĳ����Ʒ
      del : function ( id, subId ) {
         var t = this;
         var yes = confirm("�Ƿ�Ҫɾ���ò�Ʒ��");
         if ( !yes ) {
            return;
         }
         $( "#" + id + subId ).remove(); // ɾ���ýڵ�
         for ( var i = 0; i < t.cartArr.length; i ++ ) {
            if ( id == t.cartArr[i].id && subId == t.cartArr[i].industryId ) {
               t.cartArr.splice( i, 1 );
            }
         };
         t.getTotal();
         Rookie(function(){
            this.write( 'cart', t.cartArr );
         });
         t.getFormData();// ����form��param����
      },
      // ��Ʒ������һ
      plus : function ( id, subId ) {
         var t = this,
             num = 0;
         subId = subId? subId: "";
         num = Number($( "#" + id + subId + "num" ).text());
         //num = Number($( "#" + id + "num" ).text());
         num += 1;
         $( "#" + id + subId + "num" ).text( num );
         t.motify( id, "num", num, subId );
         t.getFormData();// ����form��param����
      },
      // ��Ʒ������һ
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
         t.getFormData();// ����form��param����
      },
      // �޸�����
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
      // չʾ����
      show : function ( pay ) {
         /**
          * pObj: {0002:{id:0002,title:"����ʦ�ɹ���",intro:"�ײ�",price:10},0003:{...}}
          */
         var t = this,
             cartTb = $("#cartTb"),
             pid = "", // ��Ʒid
             num = 1, // ��Ʒ����
             price = 0, // �۸�
             totalN = 0, // ������
             totalP = 0, // �ܼ۸�
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
                  
                  // ����ҵ��
                  if( industyId ) {
                     var str = "";
                     if( !$("#"+pid).length ) {
                        str += '<tr id="' + pid + '">';
                        str += '<td><span id="' + pid + 'td1" class="pd-l-10" onclick="cart.expand(\'' + pid + '\')"><span title="չ��" class="operation plusBtn">+</span></span></td>';
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
                        str += '<td>' + t.pObj[pid].description + '</td>';
                     }
                     if ( pay ) {
                        str += '<td><span id="' + pid + industyId + 'num">' + num + '</span></td>';
                        str += '<td><span class="price">' + price + '</span>Ԫ</td>';
                     } else {
                        str += '<td><span class="operation subBtn" onclick="cart.sub(\'' + pid + '\',\'' + industyId + '\')">-</span><span id="' + pid + industyId + 'num">' + num + '</span><span class="operation plusBtn" onclick="cart.plus(\'' + pid + '\',\'' + industyId + '\')">+</span></td>';
                        str += '<td><span class="price">' + price + '</span>Ԫ</td>';
                        str += '<td><a onclick="cart.del(\'' + pid + '\',\'' + industyId + '\')" href="javascript:;">ɾ��</a></td>';
                     }
                     
                  } else {
                     var str = '<tr id="' + pid + '">';
                     str += '<td><span class="pd-l-10">' + pid.split("p")[1] + '</span></td>';
                     str += '<td>' + t.pObj[pid].name + '</td>';
                     str += '<td>' + t.pObj[pid].description + '</td>';
                     if ( pay ) {
                        str += '<td><span id="' + pid + 'num">' + num + '</span></td>';
                        str += '<td><span class="price">' + price + '</span>Ԫ</td>';
                     } else {
                        str += '<td><span class="operation subBtn" onclick="cart.sub(\'' + pid + '\')">-</span><span id="' + pid + 'num">' + num + '</span><span class="operation plusBtn" onclick="cart.plus(\'' + pid + '\')">+</span></td>';
                        str += '<td><span class="price">' + price + '</span>Ԫ</td>';
                        str += '<td><a onclick="cart.del(\'' + pid + '\',\'\')" href="javascript:;">ɾ��</a></td>';
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
      // չ��
      expand : function ( id ) {
         var t = this,
             child1 = $("." + id + "-child")[0];
         if ( $(child1).hasClass("hidden") ) {
            $("#" + id + "td1").html('<span title="�۵�" class="operation subBtn">-</span>');
            $("." + id + "-child").removeClass("hidden");
         } else {
            $("#" + id + "td1").html('<span title="չ��" class="operation plusBtn">+</span>');
            $("." + id + "-child").addClass("hidden");
         }
         
      },
      // �������ݸ�ʽ���Ա��ύ����̨
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
      // ������ʾ��
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
         return;
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
   city = {"province":["ʡ/ֱϽ��", "����", "����", "����", "����", "����", "�㶫", "����", "����", "����", "�ӱ�", "������", "����", "����", "����", "���ɹ�", "����", "����", "����", "����", "����", "�ຣ", "ɽ��", "ɽ��", "�Ϻ�", "�Ĵ�", "���", "����", "�½�", "����", "�㽭", "����", "̨��", "���", "����", "����", "����"],"city":["����/����","�Ϸ�,�ߺ�,����,����,��ɽ,����,ͭ��,����,��ɽ,����,����,����,����,����,����,����,����","������,������,������,������,������,��̨��,ʯ��ɽ��,������,��ͷ����,��ɽ��,ͨ����,˳����,��ƽ��,������,������,ƽ����,������,������","������,������,������,��ɿ���,������,ɳƺ����,��������,�ϰ���,������,��ʢ��,˫����,�山��,������,ǭ����,������,�뽭��,������,ͭ����,������,�ٲ���,�ɽ��,��ƽ��,�ǿ���,�ᶼ��,�潭��,��¡��,����,����,������,�����,��ɽ��,��Ϫ��,ʯ��������������,��ɽ����������������,��������������������,��ˮ����������������,������,�ϴ���,������,�ϴ���","����,����,����,����,Ȫ��,����,��ƽ,����,����","����,������,���,����,��ˮ,����,��Ҵ,ƽ��,��Ȫ,����,����,¤��,����,����","����,�ع�,����,�麣,��ͷ,��ɽ,����,տ��,ï��,����,����,÷��,��β,��Դ,����,��Զ,��ݸ,��ɽ,����,����,�Ƹ�","����,����,����,����,����,���Ǹ�,����,���,����,��ɫ,����,�ӳ�","����,����ˮ,����,��˳,ͭ��,ǭ����,�Ͻ�,ǭ����,ǭ��","����,����,����","ʯ��ׯ,��ɽ,�ػʵ�,����,��̨,����,�żҿ�,�е�,����,�ȷ�,��ˮ","������,�������,����,�׸�,˫Ѽɽ,����,����,��ľ˹,��̨��,ĵ����,�ں�,�绯,���˰���","֣��,����,����,ƽ��ɽ,����,�ױ�,����,����,���,���,���,����Ͽ,����,����,����,�ܿ�,פ���","�人,��ʯ,ʮ��,�˲�,�差,����,����,Т��,����,�Ƹ�,����,����,��ʩ����������������","��ɳ,����,��̶,����,����,����,����,�żҽ�,����,����,����,����,¦��,��������������������","���ͺ���,��ͷ,�ں�,���,ͨ��,������˹,���ױ���,�˰���,���ֹ�����,�����첼��,�����׶���,��������","�Ͼ�,����,����,����,����,��ͨ,���Ƹ�,����,�γ�,����,��,̩��,��Ǩ","�ϲ�,������,Ƽ��,�Ž�,����,ӥ̶,����,����,�˴�,����,����","����,����,��ƽ,��Դ,ͨ��,��ɽ,��ԭ,�׳�,�ӱ߳�����������","����,����,��ɽ,��˳,��Ϫ,����,����,Ӫ��,����,����,�̽�,����,����,��«��","����,ʯ��ɽ,����,��ԭ","����,����,����,����,����,����,����,����","̫ԭ,��ͬ,��Ȫ,����,����,˷��,����,�˳�,����,�ٷ�,����","����,�ൺ,�Ͳ�,��ׯ,��Ӫ,��̨,Ϋ��,����,̩��,����,����,����,����,����,�ĳ�,����,����","������,¬����,�����,������,������,������,բ����,�����,������,������,��ɽ��,�ζ���,�ֶ�����,��ɽ��,�ɽ���,������,�ϻ���,������,������","�ɶ�,�Թ�,��֦��,����,����,����,��Ԫ,����,�ڽ�,��ɽ,�ϳ�,üɽ,�˱�,�㰲,����,�Ű�,����,����,����,����,��ɽ","��ƽ��,�Ӷ���,������,�Ͽ���,�ӱ���,������,������,������,�����,������,������,������,������,������,������,������,������,����","����,����,ɽ��,�տ���,����,����,��֥","��³ľ��,��������,��³��,����,����,��������,��������,������,��������,��ʲ,����,����,����,����̩,ʯ����","����,����,��Ϫ,��ɽ,��ͨ,����,���,��ɽ,˼é,��˫����,����,�º�,����,ŭ��,����,�ٲ�","����,����,����,����,����,����,��,����,��ɽ,̨��,��ˮ","����,ͭ��,����,����,μ��,�Ӱ�,����,����,����,����","̨��,����,����","���","����","����,Ӣ��,����,����˹,���ô�,����,�Ĵ�����,ӡ��,̩��,��������,�¼���,���ɱ�,Խ��,ӡ��,�ձ�,����",'����']}
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
      // ����ʡ��
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
      // ���س�����Ϣ
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
      // ��֤�ʼ���ʽ
      checkEmail : function () {
         var t = this;
         validator.checkEmail( t.emailNode, t.emailTipNode );
      },
      // ��֤�������ʽ
      checkOldPass : function () {
         var t = this;
         validator.checkPass( t.oldPassNode, t.oldPassTipNode );
      },
      // ��֤�������ʽ
      checkNewPass : function () {
         var t = this;
         validator.checkPass( t.newPassNode, t.newPassTipNode );
      },
      // ��֤��������������
      checkNewPass2 : function () {
         var t = this;
         validator.checkPass2( t.newPassNode, t.newPass2Node, t.newPass2TipNode );
      },
      // ��֤�ǳ�
      checkNick : function () {
         var t = this;
         validator.checkNick( t.nickNode, t.nickTipNode );
      },
      // ��֤�ֻ�
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
         var t = this;
         validator.checkEmail( t.emailNode, t.emailTipNode );
      },
      // ��֤�����ʽ
      checkPass : function () {
         var t = this;
         validator.checkVal( t.pass1Node, t.pass1TipNode );
      },
      // ��֤������������
      checkPass2 : function () {
         var t = this;
         validator.checkPass2( t.pass1Node, t.pass1Node, t.pass2TipNode );
      },
      // ��֤�ǳ�
      checkNick : function () {
         var t = this;
         validator.checkNick( t.nicknameNode, t.nicknameTipNode );
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