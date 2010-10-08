function init(holder,w,h,id,recname){
						var labels = [],
						data = [];
						data2 = [];
						pointArr1 = [];
						pointArr2 = [];
						var winWidth = w;
						var winHeight = h;
						var integer = 0;
						var num = 0;
						var point = 0;
						var paper = Raphael(holder, winWidth+100, winHeight+20);
						var blueLine = {stroke: "#547CAF", "stroke-width": 3, "stroke-linejoin": "round"};
						var greenLine = {stroke: "#53AFAC", "stroke-width": 3, "stroke-linejoin": "round"};
						var grayLine = {stroke: "#CCC", "stroke-width": 0.5, "stroke-linejoin": "round"};
						var txt = {"font": '11px Verdana', stroke: "none", fill: "#000", "text-anchor": "start"};
						var datatxt;
						var dataFormat = {"font": '12px Verdana', stroke: "none", fill: "#FFF", "text-anchor": "start"}
						
						$("#"+id+" th").each(function () {
							labels.push($(this).html());
							});
						$("#"+id+" tr:eq(1) td").each(function () {
							num = Number($(this).html().split(",")[0]);
							integer = parseInt(num);
							point = Math.round((num-integer)*100);
							data.push(point);
							pointArr1.push($(this).html());
							});
						$("#"+id+" tr:eq(2) td").each(function () {
							num = Number($(this).html().split(",")[0]);
							integer = parseInt(num);
							point = Math.round((num-integer)*100);
							data2.push(point);
							pointArr2.push($(this).html());
							});
						for(j=0;j<10;j++){
							var gray = paper.path("M50 "+j*(winHeight/10)+" L"+winWidth+50+" "+j*(winHeight/10));
							gray.attr(grayLine);
							//var dataText = paper.text(0,j*(winHeight/10),"4."+(10-j)+"0").attr(txt);
							}
						var tip1 = paper.image("img/tip.gif", -134, 0, 134, 45);
						var tip2 = paper.image("img/tip2.gif", -134, 0, 134, 45);
				    for(i=0;i<data.length;i++){
							point = winHeight-10-data[i];
							point2 = winHeight-10-data[i+1];
							var green = paper.path("M"+(i*(winWidth/(data.length-1))+50)+" "+(winHeight-10-data2[i])+"L"+(50+(i+1)*(winWidth/(data.length-1)))+" "+(winHeight-10-data2[i+1]));
				      green.attr(greenLine);
							var dot1 = paper.circle(50+i*(winWidth/(data.length-1)), (winHeight-10-data2[i]), 3).attr(greenLine);
							dot1.attr({fill: "#53AFAC"});
							dot1.num = "日期:"+labels[i]+"\n点数:"+pointArr1[i].split(",")[0]+"比率:"+pointArr1[i].split(",")[1];
							dot1.posX = 50+i*(winWidth/(data.length-1));
							dot1.posY = (winHeight-10-data2[i]);
							var blue = paper.path("M"+(i*(winWidth/(data.length-1))+50)+" "+(winHeight-10-data[i])+"L"+(50+(i+1)*(winWidth/(data.length-1)))+" "+(winHeight-10-data[i+1]));
				      blue.attr(blueLine);
					    var dot2 = paper.circle(50+i*(winWidth/(data.length-1)), point, 3).attr(blueLine);
							dot2.attr({fill: "#547CAF"});
							dot2.num = "日期:"+labels[i]+"\n点数:"+pointArr2[i].split(",")[0]+"比率:"+pointArr2[i].split(",")[1];
							dot2.posX = 50+i*(winWidth/(data.length-1));
							dot2.posY = (point);
							//paper.text(50+i*(winWidth/(data.length-1)),(winHeight-10-data[i])-10,pointArr1[i].split(",")[0]+"\n"+data[i]);
							//paper.text(50+i*(winWidth/(data2.length-1)),(winHeight-10-data2[i])-10,pointArr2[i].split(",")[0]+"\n"+data2[i]);
							var dateText = paper.text(50+i*(winWidth/(data2.length-1)),(winHeight-10)+10,labels[i])//.attr(txt);
							tip1.attr({opacity: 0});
							tip2.attr({opacity: 0});
							dot1.hover(
							  function () {
							  var t = this;
								this.attr({fill: "#FFF"});
								this.animate({scale:1.8}, 600, "backOut");
								tip1.animate({x:t.posX-15, y:t.posY-48, opacity: 0.9}, 1000, "backOut");
								datatxt = paper.text(t.posX-10,t.posY-25,t.num).attr({opacity: 0});
								datatxt.animate({opacity: 1},1000);
								datatxt.attr(dataFormat);
							  },
							  function () {
								this.attr({fill: "#53AFAC"});
								this.animate({scale:1}, 1000, "elastic");
								tip1.animate({opacity: 0}, 1000, "elastic");
								datatxt.remove();
							  });
							dot2.hover(
							  function () {
							  var t = this;
								this.attr({fill: "#FFF"});
								this.animate({scale:1.8}, 600, "backOut");
								tip2.animate({x:t.posX-15,y:t.posY-48, opacity: 0.9}, 1000, "backOut");
								datatxt = paper.text(t.posX-10,t.posY-25,t.num).attr({opacity: 0});
								datatxt.animate({opacity: 1},1000);
								datatxt.attr(dataFormat);
							  },
							  function () {
								this.attr({fill: "#547CAF"});
								this.animate({scale:1}, 1000, "elastic");
								tip2.animate({opacity: 0}, 1000, "elastic");
								datatxt.remove();
							  });
				      }
							paper.rect(60,winHeight+10,10,10).attr({fill: "#53AFAC", stroke: "none"})
							paper.rect(230,winHeight+10,10,10).attr({fill: "#547CAF", stroke: "none"})
							paper.text(140,winHeight+15,recname).attr({"font": "12px"});
							paper.text(280,winHeight+15,"大盘涨跌幅度").attr({"font": "12px"})
						}