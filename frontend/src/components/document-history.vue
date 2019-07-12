<template>
<div>
    <navmenu></navmenu>
    <div id="editor">
        <div id="title-group" align="left">
            <el-button type="text" icon="el-icon-arrow-left" style="font-size:20px;color:black;margin-left:20px;" @click="toDocumentEdit">返回文档编辑</el-button>
            <el-tag style="margin-left:30px;">时间：{{timestamp}}</el-tag>
        </div> 
        <el-button type="danger" size="medium" @click="revert" id="revert"><span class="iconfont icon-revert" style="font-size:15px;margin-right:5px;"></span>revert</el-button>
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
export default {
    name: 'document-history',
    data() {
        return {
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
            docName: '',
            timestamp: '',
            userOptions: []
        }
    },
    mounted() {
        (document.getElementById('loading')).style.display = "none"
        this.$refs.myQuillEditor.quill.enable(false)
        console.log(this.$route.query.timeStamp)
        console.log(this.$route.query.filePath)
        this.$axios(
        {
            url:'/version-manage/show-edit',
            method:"post",
            data:{
                timestamp: this.$route.query.timeStamp,
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
            console.log(response.data.editLog)
            console.log(response.data.content)
            this.docName = this.$route.query.docName
            this.timestamp = this.$route.query.timeStamp
            await this.changeContent(response.data.content)
            this.$refs.myQuillEditor.quill.enable(false)
        });
    },
    methods: {
        async changeContent(content) {
            this.$refs.myQuillEditor.quill.insertText(0,content)
        },
        toDocumentEdit() {
            this.$router.push({
                path: '/document-edit'
            })
        },
        revert() {
            this.$axios(
            {
                url:'/version-manage/revert',
                method:"post",
                data:{
                    username: window.sessionStorage.username,
                    filepath: this.$store.state.doc.oldPath,
                    timestamp: this.$route.query.timeStamp
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
                        message: '回退成功',
                        type: 'success',
                        duration: 2000
                    })
                }
                this.$router.push({
                    path: '/document-edit'
                })
            })
            }
    },
    components: { navmenu }
}
</script>

<style scoped>
.userblock {
    width: 15px;
    height: 15px;
    top: 3px;
    left: 20px;
    display: inline-block;
    position: relative;
}
#user-group {
    position: absolute;
    left: 10%;
    top: 10%;
}
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
#revert {
    position: absolute;
    top: 5%;
    left: 85%;
}
.quill-editor {
    position:absolute;
    left: 20%;
    top: 15%;
    min-height: 200px;
    height: 400px;
    width: 900px;
    min-width: 900px;
}
</style>