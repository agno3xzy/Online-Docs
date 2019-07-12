<template>
<div>
    <navmenu></navmenu>
    <div id="editor">
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
    </div>
</div>
</template>

<script>
import navmenu from './nav-menu'
import { CurentTime } from '../../static/js/DiffToStringArray'
export default {
    name: 'document-edit',
    data() {
        return {
            docName: '',
            docID: '',
            docOwner: '',
            content: '',
            userString: '',
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
            timer: ''
        }
    },
    components: {navmenu},
    methods: {
        toDocumentManage() {
            this.$store.commit('removeFilePath')
            this.$router.push({
                path: '/document-manage'
            })
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
            this.$refs.myQuillEditor.quill.enable(false)
            this.initWebSocket()
        });
    }
}
</script>

<style scoped>
#editor{
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
.quill-editor {
    position:absolute;
    left: 25%;
    top: 15%;
    min-height: 200px;
    height: 400px;
    width: 900px;
    min-width: 900px;
}
</style>