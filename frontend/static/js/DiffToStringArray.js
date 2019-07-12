function CurentTime()
{
    var now = new Date();

    var year = now.getFullYear();       //年
    var month = now.getMonth() + 1;     //月
    var day = now.getDate();            //日

    var hh = now.getHours();            //时
    var mm = now.getMinutes();          //分
    var ss = now.getSeconds();           //秒

    var clock = year + "-";

    if(month < 10)
        clock += "0";

    clock += month + "-";

    if(day < 10)
        clock += "0";

    clock += day + " ";

    if(hh < 10)
        clock += "0";

    clock += hh + ":";
    if (mm < 10) clock += '0';
    clock += mm + ":";

    if (ss < 10) clock += '0';
    clock += ss;
    return(clock);
}

function replaceAll(str1,str2)
{
    while(str1.indexOf(str2) !== -1)
    {
        str1 = str1.replace(str2,"");
    }
    return str1;
}

function loadContent() {
    content = document.getElementById("existFileContent").innerHTML;
    quill.setText(content);
    quill.setSelection(0);
    content = document.getElementById('editor-container').children[0].innerHTML;
}

function update(diff) {
    
    var linecount = 0;
    var position = 0;//记录删除的插入的位置
    //var diff = Diff.diffChars(content.replace('<br>',''),new_content.replace('<br>',''))
    //console.log(new_content.replace('<br>',''));
    var result = ''; //最终发送给后端的数据 格式:"ins"/"del","line","startposition","content".
    for(var i = 0; i < diff.length; i++)
    {
        var difflist = Array.from(diff[i]); //格式:[插入(1)/删除(-1)/相等(0),对应的字符串]
        var difftype = difflist[0]; //0：相等 1：插入 -1：删除
        var diffstring = difflist[1];
        var re = new RegExp("<p>","g");
        var re2 = new RegExp("</p>","g");
        var startcount = 0; //<p>出现的次数
        var endcount = 0; //</p>出现的次数
        if(diffstring.match(re) != null)
        {
            startcount = diffstring.match(re).length;
        }
        if(diffstring.match(re2) != null)
        {
            endcount = diffstring.match(re2).length;
        }
        if(diffstring.replace('\n','') !== '') //可能会出现一个单独的换行符
        {
            if(difftype === 1) //如果是插入操作 四种情况 aaa </p><p>aaa</p><p>bbb aaa</p><p>bbb</p><p> <p>aaa</p><p>bbb</p>
            {
                if(startcount === 0) //还是同一行的操作 类似于 aaa或者aaa</p>
                {
                    if(endcount === 0) //aaa的情况
                    {
                        var beforedifflist = Array.from(diff[i-1]);
                        var nextdifflist = Array.from(diff[i+1]);
                        var beforediffstring = beforedifflist[1];
                        var nextdiffstring = nextdifflist[1];
                        var beforedifftype = beforedifflist[0];
                        var nextdifftype = nextdifflist[0];
                        // if(beforediffstring.lastIndexOf("<p>") === beforediffstring.length - 3 && nextdiffstring.indexOf("</p>") == 0
                        //     && beforedifftype === 0 && nextdifftype === 0)
                        // {
                        //     //console.log("+1");
                        //     position = 0;
                        //     linecount += 1;
                        // }
                        result = result + "ins," + linecount.toString() + "," + position.toString() + "," + diffstring.toString() + ",false.";
                        //position += diffstring.length; //位置向后挪
                    }
                } else //分行
                {
                    if(diffstring.indexOf("<p>") === 0 && diffstring.lastIndexOf("</p>") === diffstring.length - 4) //<p>aaa</p>
                    {
                        diffstring = replaceAll(diffstring,"<p>");
                        var textlist = diffstring.split("</p>");
                        textlist.splice(textlist.length-1,1);
                        for(var j = 0; j < textlist.length; j++)
                        {
                            result = result + "ins," + linecount.toString() + "," + position.toString() + "," + textlist[j].toString() + ",true.";
                        }
                    }
                    if(diffstring.indexOf("</p><p>") !== -1) //在中间新起一行 三种情况 新起一行内容为空或者新起一行插入新内容或者把原有一行的一部分换行
                    {
                        var beforedifflist = Array.from(diff[i-1]);
                        var nextdifflist = Array.from(diff[i+1]);
                        var beforediffstring = beforedifflist[1];
                        var nextdiffstring = nextdifflist[1];
                        var beforedifftype = beforedifflist[0];
                        var nextdifftype = nextdifflist[0];
                        if(diffstring === '</p><p>') //说明新起一行内容为空
                        {
                            if(beforedifftype === 0 && beforediffstring.lastIndexOf("<p>") === beforediffstring.length - 3) //说明是新起一行
                            {
                                result = result + "ins," + linecount.toString() + "," + position.toString() + "," + "" + ",true.";
                            } else
                            {
                                //console.log(nextdiffstring.indexOf("</p>"));
                                var alterstring = nextdiffstring.slice(0,nextdiffstring.indexOf("</p>"));
                                //console.log(alterstring);
                                result = result + "del," + linecount.toString() + "," + position.toString() + "," + alterstring.toString() + ",false.";
                                position = 0;
                                result = result + "ins," + linecount.toString() + "," + position.toString() + "," + alterstring.toString() + ",true.";
                            }
                        } else
                        {
                            result = result + "ins," + linecount.toString() + "," + position.toString() + "," + diffstring.replace('</p><p>','') + ",true.";
                        }
                        
                    }
                    position = 0;
                }
            } else if (difftype === 0) //相等的匹配操作
            {
                if(startcount === 0) //aaa或者 aaa</p>
                {
                    if(endcount === 0) //aaa 另一种情况不需要操作
                    {
                        position += diffstring.length;
                    }
                } else //有<p>标签
                {
                    if(endcount === 0) //<p>aaa或者 aaa<p>bbb
                    {
                        var textlist = diffstring.split("<p>");
                        linecount += 1;
                        position = textlist[1].length;
                    } else //有</p>标签
                    {
                        linecount += startcount; //<p>标签的个数等于新的行数
                        if(diffstring.lastIndexOf("</p>") !== diffstring.length - 4)
                        {
                            if(diffstring.lastIndexOf("<p>") === diffstring.length - 3 && i !== diff.length - 1) //说明有下一条信息 需要进行判断最后的新建行是否为插入操作
                            {
                                if(Array.from(diff[i+1])[0] === 1 && Array.from(diff[i+1])[1].indexOf('</p><p>') !== -1) //如果下一行是插入操作 则行数-1
                                {
                                    console.log("-1");
                                    linecount -= 1;
                                }
                            }
                            diffstring = replaceAll(diffstring,"<p>");
                            var textlist = diffstring.split("</p>");
                            position = textlist[textlist.length-1].length;
                        }
                    }
                }
            } else if(difftype === -1) //删除操作
            {
                if(startcount === 0 && endcount === 0)
                {
                    result = result + "del," + linecount.toString() + "," + position.toString() + "," + diffstring.toString() + ",false.";
                }
                else
                {
                    if(diffstring.indexOf("<p>") === 0 && diffstring.lastIndexOf("</p>") === diffstring.length - 4) //删除的整行
                    {
                        if(startcount === 1 && endcount === 1) //只删除一行
                        {
                            linecount += 1;
                            result = result + "del," + linecount.toString() + "," + position.toString() + "," + "" + ",true.";
                        } else //删除多行
                        {
                            diffstring = replaceAll(diffstring,"<p>");
                            var textlist = diffstring.split("</p>");
                            textlist.splice(textlist.length-1,1); //最后会多一个空字符串
                            for(var j = 0; j < textlist.length; j++)
                            {
                                linecount += 1;
                                result = result + "del," + linecount.toString() + "," + position.toString() + "," + textlist[j].toString() + ",true.";
                            }
                        }
                    } else
                    {
                        var textlist = diffstring.split("</p><p>");
                        var beforedifflist = Array.from(diff[i-1]);
                        var nextdifflist = Array.from(diff[i+1]);
                        var beforediffstring = beforedifflist[1];
                        var nextdiffstring = nextdifflist[1];
                        var beforedifftype = beforedifflist[0];
                        var nextdifftype = nextdifflist[0];
                        if(beforedifftype === 0 && nextdifftype === 0)
                        {
                            if(diffstring === "</p><p>") //只把下一行切到本行
                            {
                                linecount += 1;
                                result = result + "del," + linecount.toString() + "," + "0" + "," + nextdiffstring.slice(0,nextdiffstring.indexOf("</p>")) + ",true.";
                                result = result + "ins," + (linecount-1).toString() + "," + position.toString() + "," + nextdiffstring.slice(0,nextdiffstring.indexOf("</p>")) + ",false.";
                                position = 0;
                            } else
                            {
                                if(beforediffstring.lastIndexOf("<p>") === beforediffstring.length - 3 && nextdiffstring.indexOf("</p>") === 0)
                                {
                                    for(var j = 0; j < textlist.length - 1; j++)
                                    {
                                        result = result + "del," + linecount.toString() + "," + position.toString() + "," + textlist[j].toString() + ",true.";
                                        linecount += 1;
                                    }
                                    //result = result + "del," + linecount.toString() + "," + position.toString() + "," + textlist[textlist.length-1].toString() + ",false.";
                                } else
                                {
                                    var isFirstLineDel = true; //第一行是否完全删除
                                    var firstdelline = linecount; //删除的第一行位置
                                    var appendposition = position;
                                    if(beforediffstring.lastIndexOf("<p>") === beforediffstring.length - 3)
                                    {
                                        result = result + "del," + linecount.toString() + "," + position.toString() + "," + textlist[0].toString() + ",true.";
                                    } else
                                    {
                                        result = result + "del," + linecount.toString() + "," + position.toString() + "," + textlist[0].toString() + ",false.";
                                        position = 0;
                                        isFirstLineDel = false;
                                    }
                                    for(var j = 1; j < textlist.length - 1; j++)
                                    {
                                        linecount += 1;
                                        result = result + "del," + linecount.toString() + "," + position.toString() + "," + textlist[j].toString() + ",true.";
                                    }
                                    linecount += 1;
                                    if(nextdiffstring.indexOf("</p>") === 0)
                                    {
                                        result = result + "del," + linecount.toString() + "," + position.toString() + "," + textlist[textlist.length-1].toString() + ",true.";
                                    } else
                                    {
                                        if(isFirstLineDel)
                                        {
                                            result = result + "del," + linecount.toString() + "," + position.toString() + "," + textlist[textlist.length-1].toString() + ",false.";
                                        } else
                                        {
                                            var appendstring = nextdiffstring.slice(0,nextdiffstring.indexOf("</p>"));
                                            result = result + "del," + linecount.toString() + "," + position.toString() + "," + textlist[textlist.length-1].toString() + appendstring + ",true.";
                                            result = result + "ins," + firstdelline.toString() + "," + appendposition.toString() + "," + appendstring.toString() + ",false.";
                                        }
                                    }
                                }
                            }

                        }
                    }
                }
            }
        }
    }
    return result

}

export {CurentTime,replaceAll,loadContent,update}