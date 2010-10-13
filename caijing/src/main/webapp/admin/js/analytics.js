/*
**@Author ginuim@gmail.com
**@init(�ⲿ����ID,ָ�����,ָ���߶�,������Դ����ID,��һ֧��Ʊ������,�ڶ�ֻ��Ʊ������,�Ƿ���ʾԲ�㼰��������)
*/
function init(holder,w,h,id,name1,name2,dot){
	$("#"+holder).empty();//������ⲿ����
	var labels = ["0"],//ʱ������
	percentArr1 = ["0"];//��������1
	percentArr2 = ["0"];//��������2
	pointArr1 = ["0,0%"];//ԭʼ��������1
	pointArr2 = ["0,0%"];//ԭʼ��������2
	var winWidth = w - 100;//�����Ŀ��
	var winHeight = h - 40;//�����ĸ߶�
	var num = "";//ÿ����Ԫ�ذ����Ĳ�������
	var paper = Raphael(holder, winWidth + 100, winHeight + 40);//����һ������
	
	var blueLine = {stroke: "#547CAF", "stroke-width": 3, "stroke-linejoin": "round", "fill": "#547CAF"};//������ʽ
	var greenLine = {stroke: "#53AFAC", "stroke-width": 3, "stroke-linejoin": "round", "fill": "#53AFAC"};//������ʽ	
	var grayLine = {stroke: "#CCC", "stroke-width": 0.5, "stroke-linejoin": "round"};//����������ʽ
	var grayLine2 = {stroke: "#DDD", "stroke-width": 0.5, "stroke-linejoin": "round"};//����������ʽ
	
	var txt = {"font": '11px Arial', stroke: "none", fill: "#666", "text-anchor": "start"};//��ʾ������ʽ
	var txt2 = {"font": '10px Verdana', stroke: "none", fill: "#666", "text-anchor": "end"};//��ʾ������ʽ
	var dataFormat = {"font": '12px Verdana', stroke: "none", fill: "#FFF", "text-anchor": "start"};//��ʾ���ݵ�������ʽ
	var datatxt;//��ʾ����
	var Multiple_w = 1;//��ȵı���
	var Multiple_h = 1;//�߶ȵı���
	var maxPercent = 0;//������
	var minPercent = 0;//��С����
	
	var point1_cur ,
		point1_next ,
		point2_cur ,
		point2_next ,
		x_cur ,
		x_next = 0;
	///�����ǳ���
	var minWidth = 35;//��С���
	var minHeightCount = 10;//�������ٿ̶���
	var percentCount = 4;//������ʱ�����λ�����е���ʱ��Ҫ�Ķ���86��paper.text()�ĵ�һ������
	///�������
	for (var i = 0; i < $("#" + id + " th").length; i++) {
		labels.push($("#" + id + " th:eq(" + i + ")").html());
		pointArr1.push($("#" + id + " tr:eq(1) td:eq(" + i + ")").html());
		pointArr2.push($("#" + id + " tr:eq(2) td:eq(" + i + ")").html());
		percentArr1.push(Number(pointArr1[i + 1].split(",")[1].split("%")[0]));
		percentArr2.push(Number(pointArr2[i + 1].split(",")[1].split("%")[0]));
		///ȡ�������ʺ���С����
		if(percentArr1[i + 1] > maxPercent) maxPercent = percentArr1[i + 1];
		if(percentArr2[i + 1] > maxPercent) maxPercent = percentArr2[i + 1];
		if(percentArr1[i + 1] < minPercent) minPercent = percentArr1[i + 1];
		if(percentArr2[i + 1] < minPercent) minPercent = percentArr2[i + 1];
		}
	//if(maxPercent > 1) {maxPercent += 1;}
	maxPercent = Math.ceil(maxPercent);
	minPercent = Math.round(minPercent);
	///������С��ȵı���
	function countTimesW (len) {
		var i=1;
		while (winWidth/(labels.length/i)<len){
			i=i+1;
			}
		return i;
		}
	Multiple_w = countTimesW (minWidth);
	///������С�߶ȵı���
	function countTimesH (len) {
		var i = 1;
		while (winHeight / (labels.length / i) < len) {
			i = i + 1;
			}
		return i;
		}
	Multiple_h = countTimesH (minWidth);
	///���Ƴ��������
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
	///�������ͼ
	for(k = 0; k < labels.length; k ++ ) {
		point1_cur = (maxPercent - percentArr1[k]) * (winHeight / (maxPercent-minPercent)) + 10;
		point1_next = (maxPercent - percentArr1[k + 1]) * (winHeight / (maxPercent-minPercent)) + 10;
		point2_cur = (maxPercent - percentArr2[k]) * (winHeight / (maxPercent-minPercent)) + 10;
		point2_next = (maxPercent - percentArr2[k + 1]) * (winHeight / (maxPercent-minPercent)) + 10;
		x_cur = k * (winWidth / (labels.length - 1)) + 50;
		x_next = (k + 1) * (winWidth / (labels.length - 1)) + 50;
		///��ʼ�����������
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
		
		///��ʼ��������
		var green = paper.path("M" + x_cur + " " + point1_cur + "L" + x_next + " " + point1_next);
		green.attr(greenLine);
		
		var blue = paper.path("M" + x_cur + " " + point2_cur + "L" + x_next + " " + point2_next);
		blue.attr(blueLine);
		
		var tipGreen = paper.image("img/tip.gif", -134, 0, 134, 45).attr({opacity: 0});//��ʾ����1
		var tipBlue = paper.image("img/tip2.gif", -134, 0, 134, 45).attr({opacity: 0});//��ʾ����2
		///�����Ҫ��ʾԲ��ʱ
		if(dot) {
			var blueDot = paper.circle(x_cur, point2_cur, 2).attr(blueLine);
			blueDot.posX = x_cur;
			blueDot.posY = point2_cur;
			blueDot.num = "����:" + labels[k] + "\n����:" + pointArr2[k].split(",")[0] + "����:" + pointArr2[k].split(",")[1];
			
			var greenDot = paper.circle(x_cur, point1_cur,2).attr(greenLine);
			greenDot.posX = x_cur;
			greenDot.posY = point1_cur;
			greenDot.num = "����:" + labels[k] + "\n����:" + pointArr1[k].split(",")[0] + "����:" + pointArr1[k].split(",")[1];
			///����꾭������ʱ
			blueDot.hover(
				function () {
					var t = this;
				  	this.attr({fill: "#FFF"});
				  	this.animate({scale:1.8}, 600, "backOut");
					if (t.posX < (winWidth-34) & t.posY > 45) {//���½�
						tipBlue.rotate(0, true); 
						tipBlue.scale(1,1);
						tipBlue.animate({x: t.posX - 15, y: t.posY - 48, opacity: 0.9}, 1000, "backOut");
						datatxt = paper.text(t.posX - 10, t.posY - 25, t.num).attr({opacity: 0});
					} else if (t.posX > (winWidth - 34) & t.posY > 45) {//���½�
						tipBlue.rotate(0, true); 
						tipBlue.scale(-1, 1);
						tipBlue.animate({x: t.posX - 120 , y: t.posY - 48, opacity: 0.9}, 1000, "backOut");
						datatxt = paper.text(t.posX - 120, t.posY-25, t.num).attr({opacity: 0});
					} else if (t.posX > (winWidth-34) & t.posY < 45) {//���Ͻ�
						tipBlue.rotate(180, true);
						tipBlue.scale(1, 1);
						tipBlue.animate({x: t.posX - 120, y: t.posY, opacity: 0.9}, 1000, "backOut");
						datatxt = paper.text(t.posX-115, t.posY+25, t.num).attr({opacity: 0});
					} else {//���Ͻ�
						tipBlue.rotate(0, true); 
						tipBlue.scale(1, -1);
						tipBlue.animate({x: t.posX - 15, y: t.posY, opacity: 0.9}, 1000, "backOut");
						datatxt = paper.text(t.posX-10, t.posY+25, t.num).attr({opacity: 0});
					}
					datatxt.animate({opacity: 1},1000);
					datatxt.attr(dataFormat);
				},
				function () {
					this.attr({fill: "#53AFAC"});
				  	this.animate({scale: 1}, 1000, "elastic");
				  	tipBlue.animate({opacity: 0}, 1000, "elastic");
				  	datatxt.remove();
			});
			///����꾭���̵�ʱ
			greenDot.hover(
				function () {
					var t = this;
				  	t.attr({fill: "#FFF"});
				  	t.animate({scale: 1.8}, 600, "backOut");
					if (t.posX < (winWidth - 34) & t.posY > 45) {//���½�
						tipGreen.rotate(0, true); 
						tipGreen.scale(1, 1);
						tipGreen.animate({x: t.posX - 15, y: t.posY - 48, opacity: 0.9}, 1000, "backOut");
						datatxt = paper.text(t.posX - 10, t.posY - 25, t.num).attr({opacity: 0});
					} else if (t.posX > (winWidth - 34) & t.posY > 45) {//���½�
						tipGreen.rotate(0, true); 
						tipGreen.scale(-1, 1);
						tipGreen.animate({x: t.posX - 120, y: t.posY - 48, opacity: 0.9}, 1000, "backOut");
						datatxt = paper.text(t.posX-120, t.posY-25, t.num).attr({opacity: 0});
					} else if (t.posX > (winWidth - 34) & t.posY < 45) {//���Ͻ�
						tipGreen.rotate(180, true);
						tipGreen.scale(1, 1);
						tipGreen.animate({x: t.posX - 120, y: t.posY, opacity: 0.9}, 1000, "backOut");
						datatxt = paper.text(t.posX-115, t.posY+25, t.num).attr({opacity: 0});
					} else {//���Ͻ�
						tipGreen.rotate(0, true); 
						tipGreen.scale(1, -1);
						tipGreen.animate({x: t.posX - 15, y: t.posY, opacity: 0.9}, 1000, "backOut");
						datatxt = paper.text(t.posX-10, t.posY+25, t.num).attr({opacity: 0});
					}
					datatxt.animate({opacity: 1},1000);
					datatxt.attr(dataFormat);
				},
				function () {
					this.attr({fill: "#53AFAC"});
				  	this.animate({scale: 1}, 1000, "elastic");
				  	tipGreen.animate({opacity: 0}, 1000, "elastic");
				  	datatxt.remove();
				
				});
		}
		
		//paper.text(x_cur, point1_cur, percentArr1[k]);
		//paper.text(x_cur, point2_cur, percentArr2[k]);
		
		}
	///��ʼ���������ͼ��
	paper.rect(50, winHeight + 30, 10, 10).attr({fill: "#53AFAC", stroke: "none"});
	paper.rect(230, winHeight + 30, 10, 10).attr({fill: "#547CAF", stroke: "none"});
	paper.text(70, winHeight + 34, name1).attr(txt);
	paper.text(250, winHeight + 34, name2).attr(txt);
}