<template>
<div>
    <navmenu></navmenu>
    <div id="editor">
        <div id="history-menu" @mouseenter="showChange" @mouseleave="showChange" v-bind:class="{ visibleMenu: show, hiddenMenu:!show }" align="center">
            <span v-show="!showItem" class="iconfont icon-right-circle" style="position:absolute;right:5%;top:45%;font-size:30px;"></span>
            <span v-show="showItem" class="iconfont icon-left-circle" style="position:absolute;right:5%;top:45%;font-size:30px;"></span>
            <div v-bind:class="{ visibleItem: showItem, hiddenItem:!showItem }">
                <span style="margin-top:10px;display:inline-block">目前历史版本：</span>
                <div class="history-item" v-for="(val,index) in historyOptions" ref="block" v-bind:key="'historyItem'+index" style="margin-top:20px;">
                    <span style="font-size:15px;">{{val.timestamp}}</span>
                    <br/>
                    <el-button type="text" style="color:black" @click="toDocumentHistory(index)">查看 <span class="iconfont icon-lookup" style="font-size:15px;"></span></el-button>
                </div>
            </div>
        </div>
        <div id="title-group" align="left">
            <el-button type="text" icon="el-icon-arrow-left" style="font-size:20px;color:black;margin-left:20px;" @click="toDocumentManage">返回</el-button>
            <el-tag style="margin-left:30px;">文档：{{docName}}</el-tag>
            <el-tag type="success" style="margin-left:10px;">文档所有者：{{docOwner}}</el-tag>
            <el-tag type="warning" style="margin-left:10px;">共享成员：{{userString}}</el-tag>
        </div>
        <quill-editor
            v-model="content"
            ref="myQuillEditor"
            :options="editorOption">
        </quill-editor>
        <div id="button-group" align="right">
            <el-button type="success" @click="saveFile" size="medium">保存<span class="iconfont icon-save" style="font-size:10px;margin-left:5px;"></span></el-button>
            <el-button type="primary" @click="showShareDialog" size="medium">分享<span class="iconfont icon-share" style="font-size:10px;margin-left:5px;"></span></el-button>
        </div>
    </div>
    <el-dialog
        title="邀请成员"
        :visible.sync="shareDialogVisible"
        width="700px"
        @close="closeShareDialog">
        <div id="shareType" style="margin-bottom:10px;">
            请选择要分享的权限类型
            <el-radio-group v-model="shareType" size="small" style="margin-left:20px;">
                <el-radio-button label="编辑"></el-radio-button>
                <el-radio-button label="查看"></el-radio-button>
            </el-radio-group>     
        </div>
        <div id="userInputList" style="margin-top:25px;">
            <div v-for="(item,i) of userList" v-bind:key="'user'+i" style="margin-bottom:10px;margin-top:15px;">
                <el-select v-model="userList[i]" filterable placeholder="请选择">
                    <el-option
                    v-for="item in userOptions"
                    :key="item.value"
                    :label="item.label"
                    :value="item.value">
                    </el-option>
                </el-select>
                  <el-button type="danger" @click="deleteUser(i)" size="medium" style="margin-left:25px;">删除</el-button>
            </div>
        </div>
        <el-button type="primary" @click="addNewUser" size="medium" style="margin-top:10px;">新增</el-button>
        <span slot="footer" class="dialog-footer">
            <el-button type="success" @click="shareFile">生成链接</el-button>
        </span>
    </el-dialog>
</div>
</template>

<script>
import '../assets/myicon/iconfont.css'
import navmenu from './nav-menu'
import { diff_match_patch } from '../../static/js/diff_match_patch'
import { update,CurentTime,replaceAll } from '../../static/js/DiffToStringArray'
import { setTimeout, clearTimeout, setInterval, clearInterval } from 'timers';
var content, new_content
var pos //设置光标位置
var flag = true
export default {
    name: 'document-edit',
    data() {
        return {
            websock: null,
            docName: '',
            docID: '',
            docOwner: '',
            userString: '', //共享成员的字符串
            content: '',
            editorOption: { 
                placeholder: "输入任何内容，支持html",
                modules: {  
                    toolbar: [
                    ['bold', 'italic', 'underline', 'strike'],
                    ['blockquote', 'code-block'],
                    [{ 'list': 'ordered' }, { 'list': 'bullet' }],
                    [{ 'indent': '-1' }, { 'indent': '+1' }],
                    [{ 'header': [1, 2, 3, 4, 5, 6, false] }],
                    [{ 'font': [] }],
                    [{ 'color': [] }, { 'background': [] }],
                    [{ 'align': [] }],
                    ]
                }
            },
            timer: '', //定时器的id
            show: false, //展示侧边栏
            showItem: false, //展示侧边栏中的内容 稍有0.3s延迟
            shareDialogVisible: false,
            shareType: '编辑', //分享类型
            shareLink: '', //生成的分享链接
            userList: [], //这次邀请人员的数组
            userOptions: [], //所有可以邀请的人员选项
            historyOptions: [] //所有历史版本的信息 存储格式为  时间戳:背景颜色
        }
    },
    components: {navmenu},
    methods: {
        showChange() {
            this.show = !this.show
            this.showItem = !this.showItem
            //判断是否需要重新从后端发取信息
            this.$axios(
            {
                url:'/version-manage/show',
                method:"post",
                data:{
                    username: window.sessionStorage.username,
                    filepath: this.$store.state.doc.oldPath
                },
                transformRequest: [function (data) {
                // Do whatever you want to transform the data
                let ret = ''
                for (let it in data) {
                // 如果要发送中文 编码 
                    ret += encodeURIComponent(it) + '=' + encodeURIComponent(data[it]) + '&'
                }
                return ret
                }],
                headers: {
                    'Content-Type':'application/x-www-form-urlencoded'
                }
            }).catch(error => {
                console.log(error.message);
            })
            .then(async response => {
                await this.changeHistoryOptions(response.data.versionItem)
                for(var i = 0; i < this.historyOptions.length; i++)
                {
                    this.$refs['block'][i].style.backgroundColor = this.historyOptions[i].color
                }
            });
        },
        async changeHistoryOptions(versionItem) 
        {
            for(var i = this.historyOptions.length-1; i >= 0; i--)
            {
                this.$delete(this.historyOptions,i)
            }
            for(var time in versionItem)
            {
                this.historyOptions.push({timestamp:time,color:versionItem[time]})
            }
        },
        toDocumentManage() {
            this.$router.push({
                path: '/document-manage'
            })
        },
        toDocumentHistory(index) {
            this.$router.push({
                path: '/document-history',
                query: {
                    timeStamp: this.historyOptions[index].timestamp,
                    docName: this.docName
                }
            })
        },
        timeUpdate() {
            pos = this.$refs.myQuillEditor.quill.selection.savedRange.index
            new_content = this.content
            console.log('比较时:')
            console.log('content:',content)
            console.log('new_content:',new_content)
            var dmp = new diff_match_patch();
            var diff = dmp.diff_main(replaceAll(content,'<br>'),replaceAll(new_content,'<br>'));
            var opList = (update(diff))
            console.log(opList)
            console.log("diff:",diff)
            if(opList) //如果有不同的改变，则与后端通信
            {
                console.log('与后端进行通信')
                //let data = {'newPath': this.$store.state.doc.newPath}
                let data = {"opList":opList,"newPath": this.$store.state.doc.newPath,"oldPath": this.$store.state.doc.oldPath,"username": window.sessionStorage.username,"timeStamp": CurentTime()}
                this.websocketsend(JSON.stringify(data))
            }
            // this.$axios(
            // {
            //     url:'/conflictHandle',
            //     method:"post",
            //     data:{
            //         opList: opList,
            //         newPath: this.$store.state.doc.newPath,
            //         oldPath: this.$store.state.doc.oldPath,
            //         username: window.sessionStorage.username,
            //         timeStamp: CurentTime()
            //     },
            //     transformRequest: [function (data) {
            //     // Do whatever you want to transform the data
            //     let ret = ''
            //     for (let it in data) {
            //     // 如果要发送中文 编码 
            //         ret += encodeURIComponent(it) + '=' + encodeURIComponent(data[it]) + '&'
            //     }
            //     return ret
            //     }],
            //     headers: {
            //         'Content-Type':'application/x-www-form-urlencoded'
            //     }
            // }).catch(error => {
            //     console.log(error.message);
            // })
            // .then(async response => {
            //     // this.content = response.data.content
            //     // content = new_content
            //     await this.changeData(response.data.content)
            //     this.$refs.myQuillEditor.quill.setSelection(pos)
            // });
        },
        changeData(response_content)
        {
            this.$refs.myQuillEditor.quill.deleteText(0,this.content.length)
            this.$refs.myQuillEditor.quill.insertText(0,response_content)
            // console.log('改变前:')
            // console.log('content:',content)
            // console.log('new_content:',new_content)
            content = this.content
            new_content = this.content
            // console.log('改变后:')
            // console.log('content:',content)
            // console.log('new_content:',new_content)
        },
        saveFile() {
            this.$axios(
            {
                url:'/version-manage/add',
                method:"post",
                data:{
                    username: window.sessionStorage.username,
                    filepath: this.$store.state.doc.oldPath
                },
                transformRequest: [function (data) {
                // Do whatever you want to transform the data
                let ret = ''
                for (let it in data) {
                // 如果要发送中文 编码 
                    ret += encodeURIComponent(it) + '=' + encodeURIComponent(data[it]) + '&'
                }
                return ret
                }],
                headers: {
                    'Content-Type':'application/x-www-form-urlencoded'
                }
            }).catch(error => {
                console.log(error.message);
            })
            .then(response => {
                if(response.data.message === 'success')
                {
                    this.$message({
                        message: '保存成功',
                        type: 'success',
                        duration: 2000
                    }) 
                } else if(response.data.message === 'fail')
                {
                    this.$message({
                        message: '保存失败',
                        type: 'error',
                        duration: 2000
                    })
                }
            })
        },
        showShareDialog() {
            for(var i = this.userList.length-1; i >= 0; i--)
            {
                this.$delete(this.userList,i)
            }
            this.userList.push('') //放空的一个进去
            this.$axios(
            {
                url:'/share/send',
                method:"post",
                data:{
                    username: window.sessionStorage.username,
                    docName: this.docName + '.txt'
                },
                transformRequest: [function (data) {
                // Do whatever you want to transform the data
                let ret = ''
                for (let it in data) {
                // 如果要发送中文 编码 
                    ret += encodeURIComponent(it) + '=' + encodeURIComponent(data[it]) + '&'
                }
                return ret
                }],
                headers: {
                    'Content-Type':'application/x-www-form-urlencoded'
                }
            }).catch(error => {
                console.log(error.message);
            })
            .then(response => {
                for(var i = 0; i < response.data.usernameList.length; i++)
                {
                    this.userOptions.push({label:response.data.usernameList[i],value:response.data.usernameList[i]})
                }
                return (response)
            });
            this.shareDialogVisible = true
        },
        closeShareDialog() {
            for(var i = this.userOptions.length-1; i >= 0; i--)
            {
                this.$delete(this.userOptions,i)
            }
        },
        addNewUser() {
            this.userList.push('')
        },
        deleteUser(index) {
            this.$delete(this.userList,index)
        },
        shareFile() {
            var auth = ''
            if(this.shareType === '编辑')
            {
                auth = 'share'
            } else if(this.shareType === '查看')
            {
                auth = 'read'
            }
            var templist = this.userList.sort()
            var duplicateFlag = false
            for(var i = 0; i < templist.length - 1; i++)
            {
                if(templist[i] === templist[i+1])
                {
                    duplicateFlag = true
                }
            }
            if(duplicateFlag)
            {
                this.$message.error('不要重复邀请同一个人')
                return 
            }
            //与后端交互分享文件
            this.$axios(
            {
                url:'/shareInfo',
                method:"post",
                data:{
                    userList: this.userList,
                    authority: auth,
                    filePath: this.$store.state.doc.oldPath
                },
                transformRequest: [function (data) {
                // Do whatever you want to transform the data
                let ret = ''
                for (let it in data) {
                // 如果要发送中文 编码 
                    ret += encodeURIComponent(it) + '=' + encodeURIComponent(data[it]) + '&'
                }
                return ret
                }],
                headers: {
                    'Content-Type':'application/x-www-form-urlencoded'
                }
            }).catch(error => {
                console.log(error.message);
                this.$message.error('生成失败')
            })
            .then(response => {
                this.shareLink = 'http://localhost:8082/#/inviteConfirm/' + this.docID + '/' + auth
                this.$copyText(this.shareLink).then(()=> {
                this.$message({
                    message: '生成链接成功，已复制到您的剪切板上',
                    type: 'success'
                })
                this.$confirm('是否要给邀请用户发送邀请邮件?', '提示', {
                    confirmButtonText: '确定',
                    cancelButtonText: '取消',
                    type: 'warning'
                }).then(() => {
                    this.$axios(
                    {
                        url:'/mail/send-invite',
                        method:"post",
                        data:{
                            username: window.sessionStorage.username,
                            invitelist: this.userList,
                            invitelink: this.shareLink,
                            privilege: auth,
                        },
                        transformRequest: [function (data) {
                        // Do whatever you want to transform the data
                        let ret = ''
                        for (let it in data) {
                        // 如果要发送中文 编码 
                            ret += encodeURIComponent(it) + '=' + encodeURIComponent(data[it]) + '&'
                        }
                        return ret
                        }],
                        headers: {
                            'Content-Type':'application/x-www-form-urlencoded'
                        }
                    }).catch(error => {
                        console.log(error.message);
                        this.$message.error('生成失败')
                    })
                    .then(response => {
                        if(response.data.message === 'success')
                        {
                            this.$message({
                                message: '发送邮件成功',
                                type: 'success'
                            })
                        } else if(response.data.message === 'fail')
                        {
                            this.$message({
                                message: '发送失败',
                                type: 'error'
                            })
                        }
                    });

                }).catch(() => {        
                });
                },()=> {
                    //复制失败的处理
                })
            });
            this.shareDialogVisible = false
        },
        initWebSocket(){ //初始化weosocket
            const wsuri = "ws://localhost:8080/socketServer?username=" + window.sessionStorage.username + "&docName=" + this.docName + ".txt" + "&docOwner=" + this.docOwner;
            this.websock = new WebSocket(wsuri);
            this.websock.onmessage = this.websocketonmessage;
            this.websock.onopen = this.websocketonopen;
            this.websock.onerror = this.websocketonerror;
            this.websock.onclose = this.websocketclose;
        },
        websocketonopen(){ //连接建立之后执行send方法发送数据
            //this.websocketsend("success");
        },
        websocketonerror(){//连接建立失败重连
            this.initWebSocket();
        },
        websocketonmessage(e){ //数据接收
            var data = JSON.parse(e.data)
            console.log(data.content)
            this.changeData(data.content)
            this.$refs.myQuillEditor.quill.setSelection(pos)
        },
        websocketsend(Data){//数据发送
            this.websock.send(Data);
        },
        websocketclose(e){  //关闭
            console.log('断开连接',e);
        },
        handleKeyDown() {
            // ctrl + s
            var key = window.event.keyCode ? window.event.keyCode : window.event.which
            if( window.event.ctrlKey && key === 83 ){
                if(flag)
                {
                    this.saveFile()
                    flag = false
                }
                window.event.preventDefault()
            }
        },
        handleKeyUp() {
            var key = window.event.keyCode ? window.event.keyCode : window.event.which
            if( window.event.ctrlKey && key === 83 ){
                flag = true
                window.event.preventDefault()
            }
        }
    },
    created() {
        document.addEventListener('keydown', this.handleKeyDown);
        document.addEventListener('keyup',this.handleKeyUp);
    },
    mounted() {
        (document.getElementById('loading')).style.display = "none"
        //与后端通讯 获取文件内容
        //axios
        this.$axios(
        {
            url:'/edit',
            method:"post",
            data:{
                oldPath: this.$store.state.doc.oldPath,
                newPath: this.$store.state.doc.newPath
            },
            transformRequest: [function (data) {
            // Do whatever you want to transform the data
            let ret = ''
            for (let it in data) {
            // 如果要发送中文 编码 
                ret += encodeURIComponent(it) + '=' + encodeURIComponent(data[it]) + '&'
            }
            return ret
            }],
            headers: {
                'Content-Type':'application/x-www-form-urlencoded'
            }
        }).catch(error => {
            console.log(error.message);
        })
        .then(response => {
            //this.content = response.data.content
            this.$refs.myQuillEditor.quill.insertText(0,response.data.content)
            content = this.content
            pos = 0
            this.docName = response.data.docName.replace('.txt','')
            this.docID = response.data.docID
            this.docOwner = response.data.docOwner
            var userString = ''
            for(var i = 0; i < response.data.docSharer.length; i++)
            {
                userString = userString + response.data.docSharer[i] + ' '
            }
            if(userString === '')
            {
                this.userString = '无'
            } else
            {
                this.userString = userString
            }
            this.initWebSocket()
            this.timer = setInterval(this.timeUpdate, 3000)
        });
    },
    beforeDestroy() {
        clearInterval(this.timer)
    },
    destroyed() {
        this.websock.close()
        document.removeEventListener('keydown', this.handleKeyDown)
        document.removeEventListener('keyup', this.handleKeyUp)
    },
}
</script>

<style scoped>
body {
    position: absolute;
}
.visibleMenu {

    width: 300px;
    z-index: 2;
    background-color: white;
    transition: width, background-color, z-index;
    transition-duration: 0.5s;
}
.hiddenMenu {
    width: 70px;
    background-color: #F7F7F7;
    z-index: 0;
    transition: width, background-color, z-index;
    transition-duration: 0.5s;
}
.visibleItem {
    opacity: 1;
    transition: opacity;
    transition-delay: 0.3s;
    transition-duration: 1s;
}
.hiddenItem {
    opacity: 0;
    transition: opacity;
    transition-duration: 0.1s;
}
.history-item {
    width: 200px;
    box-shadow: 0 4px 8px 0 rgba(0, 0, 0, 0.2), 0 6px 20px 0 rgba(0, 0, 0, 0.19);
}
#history-menu {
    position: absolute;
    left: 0;
    top: 0;
    height: 100%;
    overflow: auto;
}
#editor {
    position: absolute;
    left: 0;
    top: 60px;
    bottom: 0;
    right: 0;
    background-color: #F7F7F7;
}
#title-group {
    position: relative;
    top: 20px;
    left: 20px;
}
#button-group {
    position: relative;
    top: 20px;
    right: 5%;
}
.quill-editor {
    position:absolute;
    left: 20%;
    top: 20%;
    min-height: 200px;
    height: 400px;
    width: 900px;
    min-width: 900px;
}
.errorMessage {
    position: absolute;
    color:red;
    left:10%;
}
</style>