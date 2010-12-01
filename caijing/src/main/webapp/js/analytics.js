/*
**@Author ginuim@gmail.com
**@init(外部容器ID,指定宽度,指定高度,数据来源表格的ID,第一支股票的名称,第二只股票的名称,是否显示圆点及提醒气泡)
*/
function init(holder,w,h,id,name1,name2,dot){
	$("#"+holder).empty();//先清空外部容器
	var labels = ["0"];//时间数组
	var percentArr1 = ["0"];//涨幅数组1
	var percentArr2 = ["0"];//涨幅数组2
	var pointArr1 = ["0,0%"];//原始数据数组1
	var pointArr2 = ["0,0%"];//原始数据数组2
	var dotsArr = [];//以星期为单位存储数据
	var daysArr = [];
	var weeksArr = [];
	var days2ShowWeek = 60;
	
	var winWidth = w - 100;//画布的宽度
	var winHeight = h - 40;//画布的高度
	var num = "";//每个点元素包含的参数内容
	var byWhich = 0;
	var paper = Raphael(holder, winWidth + 100, winHeight + 40);//声明一个画布
	
	var blueLine = {stroke: "#DC3912", "stroke-width": 2, "stroke-linejoin": "round", "fill": "#DC3912", "title" : name2};//蓝线样式
	var greenLine = {stroke: "#0066FF", "stroke-width": 2, "stroke-linejoin": "round", "fill": "#0066FF", "title" : name1};//绿线样式	
	var blueLine2 = {stroke: "#DC3912", "stroke-width": 1, "stroke-linejoin": "round", "fill": "#DC3912", "title" : name2};
	var greenLine = {stroke: "#0066FF", "stroke-width": 1, "stroke-linejoin": "round", "fill": "#0066FF", "title" : name1};//绿线样式	
	var grayLine = {stroke: "#CCC", "stroke-width": 0.5, "stroke-linejoin": "round"};//背景灰线样式
	var grayLine2 = {stroke: "#DDD", "stroke-width": 0.5, "stroke-linejoin": "round"};//背景灰线样式
	
	var txt = {"font-family": "Arial", "font-size": '11px', stroke: "none", fill: "#666", "text-anchor": "start"};//提示文字样式
	var txt2 = {"font": '10px Verdana', stroke: "none", fill: "#666", "text-anchor": "end"};//提示文字样式
	var dataFormat = {"font": '12px Verdana', stroke: "none", fill: "#FFF", "text-anchor": "start"};//显示数据的文字样式
	var datatxt;//提示文字
	var Multiple_w = 1;//宽度的倍数
	var Multiple_h = 1;//高度的倍数
	var maxPercent = 0;//最大涨幅
	var minPercent = 0;//最小涨幅
	
	var point1_cur ,
		point1_next ,
		point2_cur ,
		point2_next ,
		x_cur ,
		x_next = 0;
	///下面是常量
	var minWidth = 35;//最小宽度
	var minHeightCount = 10;//纵轴最少刻度数
	var percentCount = 4;//纵向涨幅保留的位数，有调整时需要改动第86行paper.text()的第一个参数
	///获得数据
	for (var i = 0; i < $("#" + id + " th").length; i++) {
		labels.push($("#" + id + " th:eq(" + i + ")").html().replace(/\s/mg,""));
		pointArr1.push($("#" + id + " tr:eq(1) td:eq(" + i + ")").html().replace(/\s/mg,""));
		pointArr2.push($("#" + id + " tr:eq(2) td:eq(" + i + ")").html().replace(/\s/mg,""));
		percentArr1.push(Number(pointArr1[i + 1].split(",")[1].split("%")[0]));
		percentArr2.push(Number(pointArr2[i + 1].split(",")[1].split("%")[0]));
		///取得最大涨幅和最小涨幅
		if(percentArr1[i + 1] > maxPercent) maxPercent = percentArr1[i + 1];
		if(percentArr2[i + 1] > maxPercent) maxPercent = percentArr2[i + 1];
		if(percentArr1[i + 1] < minPercent) minPercent = percentArr1[i + 1];
		if(percentArr2[i + 1] < minPercent) minPercent = percentArr2[i + 1];
		}
	//if(maxPercent > 1) {maxPercent += 1;}
	maxPercent = Math.ceil(maxPercent);
	minPercent = Math.round(minPercent);
	///计算最小宽度的倍数
	function countTimesW (len) {
		var i=1;
		while (winWidth/(labels.length/i)<len){
			i=i+1;
			}
		return i;
		}
	Multiple_w = countTimesW (minWidth);
	///计算最小高度的倍数
	function countTimesH (len) {
		var i = 1;
		while (winHeight / (labels.length / i) < len) {
			i = i + 1;
			}
		return i;
		}
	Multiple_h = countTimesH (minWidth);
	///绘制出横向格线
	for (j = 0; j <= minHeightCount; j++) {
		var tmpStr = String(minPercent + j * (maxPercent - minPercent) / minHeightCount);
		if (tmpStr.indexOf(".") == -1) {
			tmpStr += ".";
		}
		while (tmpStr.replace("-", "").length < percentCount && tmpStr.indexOf(".") != -1) {
			tmpStr += "0";
		}
		if (Number(tmpStr) == 0) {
			tmpStr = "0.000";
		}
		var finalStr = "";
		if (tmpStr.split("-")[1]) {
			finalStr = "-" + tmpStr.replace("-", "").substring(0, percentCount);
		} else {
			finalStr = tmpStr.substring(0, percentCount);
		}
		var gray = paper.path("M50 " + (winHeight - j * (winHeight / 10) + 10) + " L" + (winWidth+50) + " " + (winHeight - j * (winHeight / 10) + 10)).attr(grayLine);
		paper.text(45, (winHeight - j * (winHeight / 10) + 10),finalStr + "%").attr(txt2);
		
	}
	///绘出曲线图
	for(k = 0; k < labels.length; k ++ ) {
		point1_cur = (maxPercent - percentArr1[k]) * (winHeight / (maxPercent-minPercent)) + 10;
		point1_next = (maxPercent - percentArr1[k + 1]) * (winHeight / (maxPercent-minPercent)) + 10;
		point2_cur = (maxPercent - percentArr2[k]) * (winHeight / (maxPercent-minPercent)) + 10;
		point2_next = (maxPercent - percentArr2[k + 1]) * (winHeight / (maxPercent-minPercent)) + 10;
		x_cur = k * (winWidth / (labels.length - 1)) + 50;
		x_next = (k + 1) * (winWidth / (labels.length - 1)) + 50;
		///开始绘制纵向格线
		if(k % Multiple_w == 0 && k != 0){
			if(labels.length>10){
				paper.text(x_cur - minWidth / 2, winHeight + 20, labels[k].replace(/\w{4}-/,"")).attr(txt);
			} else {
				paper.text(x_cur - minWidth / 2, winHeight + 20, labels[k]).attr(txt);
			}
			paper.path("M" + x_cur + " 10 L" + x_cur  + " " + (winHeight + 10)).attr(grayLine);
		} else {
			paper.path("M" + x_cur + " 10 L" + x_cur  + " " + (winHeight + 10)).attr(grayLine2);
		}
		
		///开始绘制周曲线
		if(labels.length > days2ShowWeek) {
				if(k % 4 == 0) {
						var weeksGreen = paper.path("M" + x_cur + " " + point1_cur + "L" + x_next + " " + point1_next);
						weeksGreen.attr(greenLine);
						
						var weeksBlue = paper.path("M" + x_cur + " " + point2_cur + "L" + x_next + " " + point2_next);
						weeksBlue.attr(blueLine);
						
						weeksArr.push(weeksGreen, weeksBlue);
				}
		}
		
		///开始绘制日曲线
		var green = paper.path("M" + x_cur + " " + point1_cur + "L" + x_next + " " + point1_next);
		green.attr(greenLine);
		
		var blue = paper.path("M" + x_cur + " " + point2_cur + "L" + x_next + " " + point2_next);
		blue.attr(blueLine);
		
		var tipGreen = paper.image("/img/tip.gif", -180, 0, 160, 45).attr({opacity: 0});//提示气泡1
		var tipBlue = paper.image("/img/tip2.gif", -180, 0, 160, 45).attr({opacity: 0});//提示气泡2
		
		daysArr.push(blue,green);
		///如果需要显示圆点时
		if(dot && labels.length < days2ShowWeek) {
			var blueDot = paper.circle(x_cur, point2_cur, 2).attr(blueLine);
			blueDot.posX = x_cur;
			blueDot.posY = point2_cur;
			blueDot.num = "日期:" + labels[k] + "\n点数:" + pointArr2[k].split(",")[0] + "涨幅:" + pointArr2[k].split(",")[1];
			
			var greenDot = paper.circle(x_cur, point1_cur, 2).attr(greenLine);
			greenDot.posX = x_cur;
			greenDot.posY = point1_cur;
			greenDot.num = "日期:" + labels[k] + "\n点数:" + pointArr1[k].split(",")[0] + "涨幅:" + pointArr1[k].split(",")[1];
			
			daysArr.push(blueDot,greenDot);
			
			///当鼠标经过蓝点时
			blueDot.hover(
				function () {
					//alert("blueDot");
					var t = this;
				  	this.attr({fill: "#FFF"});
				  	this.animate({scale:1.8}, 600, "backOut");
					if (t.posX < (winWidth-34) & t.posY > 45) {//左下角
						tipBlue.rotate(0, true); 
						tipBlue.scale(1,1);
						tipBlue.animate({x: t.posX - 15, y: t.posY - 48, opacity: 0.9}, 1000, "backOut");
						datatxt = paper.text(t.posX - 10, t.posY - 25, t.num).attr({opacity: 0});
					} else if (t.posX > (winWidth - 34) & t.posY > 45) {//右下角
						tipBlue.rotate(0, true); 
						tipBlue.scale(-1, 1);
						tipBlue.animate({x: t.posX - 145 , y: t.posY - 48, opacity: 0.9}, 1000, "backOut");
						datatxt = paper.text(t.posX - 140, t.posY-25, t.num).attr({opacity: 0});
					} else if (t.posX > (winWidth-34) & t.posY < 45) {//右上角
						tipBlue.rotate(180, true);
						tipBlue.scale(1, 1);
						tipBlue.animate({x: t.posX - 145, y: t.posY, opacity: 0.9}, 1000, "backOut");
						datatxt = paper.text(t.posX-135, t.posY+25, t.num).attr({opacity: 0});
					} else {//左上角
						tipBlue.rotate(0, true); 
						tipBlue.scale(1, -1);
						tipBlue.animate({x: t.posX - 15, y: t.posY, opacity: 0.9}, 1000, "backOut");
						datatxt = paper.text(t.posX-10, t.posY+25, t.num).attr({opacity: 0});
					}
					datatxt.animate({opacity: 1},1000);
					datatxt.attr(dataFormat);
				},
				function () {
					this.attr({fill: "#547CAF"});
				  	this.animate({scale: 1}, 1000, "elastic");
				  	tipBlue.animate({opacity: 0, y: -50}, 1000, "elastic");
				  	datatxt.remove();
			});
			///当鼠标经过绿点时
			greenDot.hover(
				function () {
					//alert("greenDot");
					var t = this;
				  	t.attr({fill: "#FFF"});
				  	t.animate({scale: 1.8}, 600, "backOut");
					if (t.posX < (winWidth - 34) & t.posY > 45) {//左下角
						tipGreen.rotate(0, true); 
						tipGreen.scale(1, 1);
						tipGreen.animate({x: t.posX - 15, y: t.posY - 48, opacity: 0.9}, 1000, "backOut");
						datatxt = paper.text(t.posX - 10, t.posY - 25, t.num).attr({opacity: 0});
					} else if (t.posX > (winWidth - 34) & t.posY > 45) {//右下角
						tipGreen.rotate(0, true); 
						tipGreen.scale(-1, 1);
						tipGreen.animate({x: t.posX - 145, y: t.posY - 48, opacity: 0.9}, 1000, "backOut");
						datatxt = paper.text(t.posX-140, t.posY - 25, t.num).attr({opacity: 0});
					} else if (t.posX > (winWidth - 34) & t.posY < 45) {//右上角
						tipGreen.rotate(180, true);
						tipGreen.scale(1, 1);
						tipGreen.animate({x: t.posX - 145, y: t.posY, opacity: 0.9}, 1000, "backOut");
						datatxt = paper.text(t.posX - 135, t.posY+25, t.num).attr({opacity: 0});
					} else {//左上角
						tipGreen.rotate(0, true); 
						tipGreen.scale(1, -1);
						tipGreen.animate({x: t.posX - 15, y: t.posY, opacity: 0.9}, 1000, "backOut");
						datatxt = paper.text(t.posX - 10, t.posY + 25, t.num).attr({opacity: 0});
					}
					datatxt.animate({opacity: 1},1000);
					datatxt.attr(dataFormat);
				},
				function () {
					this.attr({fill: "#53AFAC"});
				  this.animate({scale: 1}, 1000, "elastic");
				  tipGreen.animate({opacity: 0, y: -50}, 1000, "elastic");
				  datatxt.remove();
				
				});
		}
		
		//paper.text(x_cur, point1_cur, percentArr1[k]);
		//paper.text(x_cur, point2_cur, percentArr2[k]);
		
	}
	if(labels.length > days2ShowWeek) {
		var byWeek = paper.rect(winWidth-150, winHeight + 30, 10, 10);
		var byDay = paper.rect(winWidth-70, winHeight + 30, 10, 10);
		paper.text(winWidth-130, winHeight + 34, "按周查看").attr(txt);
		paper.text(winWidth-50, winHeight + 34, "按天查看").attr(txt);
		byWeek.click(function (event) {
		    for( var i = 0; i < daysArr.length; i++ ) {
		    		daysArr[i].hide();
		    }
		    for( var i = 0; i < weeksArr.length; i++ ) {
		    		weeksArr[i].show();
		    }
		    byWhich = 1;
		});
		byDay.click(function (event) {
		    for( var i = 0; i < daysArr.length; i++ ) {
		    		daysArr[i].show();
		    }
		    for( var i = 0; i < weeksArr.length; i++ ) {
		    		weeksArr[i].hide();
		    }
		    byWhich = 0;
		});
	}
	///绘制出周曲线
	/*if(labels.length > days2ShowWeek) {
			for(l = 0; l < labels.length; l ++) {
					if(l % 4 == 0) {
							
					}
			}
	}*/
	/*for(l = 0; l < weekArr.length; l ++) {
		point1_cur = (maxPercent - weekArr[l]) * (winHeight / (maxPercent-minPercent)) + 10;
		point1_next = (maxPercent - weekArr[l + 1]) * (winHeight / (maxPercent-minPercent)) + 10;
		point2_cur = (maxPercent - weekArr2[l]) * (winHeight / (maxPercent-minPercent)) + 10;
		point2_next = (maxPercent - weekArr2[l + 1]) * (winHeight / (maxPercent-minPercent)) + 10;
		x_cur = l * (winWidth / (labels.length - 1)) * 7 + 50;
		x_next = (l + 1) * (winWidth / (labels.length - 1)) * 7 + 50;
		
		var green = paper.path("M" + x_cur + " " + point1_cur + "L" + x_next + " " + point1_next);
		green.attr(greenLine);
		green.hide();
		var blue = paper.path("M" + x_cur + " " + point2_cur + "L" + x_next + " " + point2_next);
		blue.attr(blueLine);
		blue.hide();
		
		if(dot) {
			var greenDot = paper.circle(x_cur, point1_cur, 2).attr(greenLine);
			greenDot.posX = x_cur;
			greenDot.posY = point1_cur;
			greenDot.hide();
			var blueDot = paper.circle(x_cur, point2_cur, 2).attr(blueLine);
			blueDot.posX = x_cur;
			blueDot.posY = point2_cur;
			blueDot.hide();
			linesArr2.push(blueDot);
			linesArr2.push(greenDot);
		}
		linesArr2.push(green);
		linesArr2.push(blue);
	}*/
	///开始绘制下面的图例
	paper.rect(50, winHeight + 30, 10, 10).attr({fill: "#0066FF", stroke: "none"});
	paper.rect(230, winHeight + 30, 10, 10).attr({fill: "#DC3912", stroke: "none"});
	paper.text(70, winHeight + 34, name1).attr(txt);
	paper.text(250, winHeight + 34, name2).attr(txt);	
}
