<template>
<div>
    <navmenu></navmenu>
    <div id="main">
        <div id="submenu">
            <a href="javascript:void(0)" v-on:click="changeCategory" v-bind:class="{active:category.lastuse}" id="lastuse">最近使用</a>
            <a href="javascript:void(0)" v-on:click="changeCategory" v-bind:class="{active:category.create}" id="create">我创建的</a>
            <a href="javascript:void(0)" v-on:click="changeCategory" v-bind:class="{active:category.invite}" id="invite">共享给我的</a>
        </div>
        <div id="lastuse-table" v-show="category.lastuse">
            <el-table 
            :header-cell-style="{background:'#F7F7F7'}"
            :cell-style="{background:'#F7F7F7'}"
            :data="lastUseTableData"
            style="width: 100%;"
            max-height="500">
                <el-table-column prop="docName" label="文档名" width="150" align="center"></el-table-column>
                <el-table-column prop="owner" label="文档所有者" width="150" align="center"></el-table-column>
                <el-table-column prop="lastUseTime" label="最近修改时间" width="200" align="center"></el-table-column>
                <el-table-column prop="auth" label="权限" width="150" align="center"></el-table-column>
                <el-table-column label="操作" width="300" align="center">
                    <template slot-scope="scope">
                        <el-button
                        size="mini"
                        @click="handleExplore(scope.$index, scope.row)">查看</el-button>
                        <el-button
                        size="mini"
                        v-show="lastUseTableData[scope.$index].auth === 'share' || lastUseTableData[scope.$index].auth === 'own'"
                        @click="handleEdit(scope.$index, scope.row)">编辑</el-button>
                        <el-button
                        type="primary"
                        size="mini"
                        @click="handleDownload(scope.$index, scope.row)">下载</el-button>
                    </template>
                </el-table-column>
            </el-table>
        </div>
        <div id="create-table" v-show="category.create">
            <el-table 
            :header-cell-style="{background:'#F7F7F7'}"
            :cell-style="{background:'#F7F7F7'}"
            :data="createTableData"
            style="width: 100%;"
            max-height="500">
                <el-table-column prop="docName" label="文档名" width="150" align="center"></el-table-column>
                <el-table-column prop="lastUseTime" label="最近修改时间" width="200" align="center"></el-table-column>
                <el-table-column prop="shareAmount" label="共享人数" width="150" align="center"></el-table-column>
                <el-table-column prop="userData" label="共享成员" width="300" align="center">
                    <template slot-scope="scope">
                        <el-tag v-for=" (item,index) in createTableData[scope.$index].userData" v-bind:key="'tag'+index" :type="generateType(index)" size="mini" style="margin-left:10px;margin-top:10px;">{{item.username}}</el-tag>
                    </template>
                </el-table-column>
                <el-table-column label="操作" width="300" align="center">
                    <template slot-scope="scope">
                        <el-button
                        size="mini"
                        @click="handleEdit(scope.$index, scope.row)">编辑</el-button>
                        <el-button
                        type="primary"
                        size="mini"
                        @click="handleDownload(scope.$index, scope.row)">下载</el-button>
                        <el-button
                        size="mini"
                        type="danger"
                        @click="handleDelete(scope.$index, scope.row)">删除</el-button>
                    </template>
                </el-table-column>
            </el-table>
        </div>
        <div id="invite-table" v-show="category.invite">
            <el-table 
            :header-cell-style="{background:'#F7F7F7'}"
            :cell-style="{background:'#F7F7F7'}"
            :data="inviteTableData"
            style="width: 100%;"
            max-height="500">
                <el-table-column prop="docName" label="文档名" width="150" align="center"></el-table-column>
                <el-table-column prop="owner" label="文档所有者" width="150" align="center"></el-table-column>
                <el-table-column prop="lastUseTime" label="最近修改时间" width="200" align="center"></el-table-column>
                <el-table-column prop="auth" label="权限" width="150" align="center"></el-table-column>
                <el-table-column label="操作" width="300" align="center">
                    <template slot-scope="scope">
                        <el-button
                        size="mini"
                        @click="handleExplore(scope.$index, scope.row)">查看</el-button>
                        <el-button
                        size="mini"
                        v-show="inviteTableData[scope.$index].auth === 'share'"
                        @click="handleEdit(scope.$index, scope.row)">编辑</el-button>
                        <el-button
                        type="primary"
                        size="mini"
                        @click="handleDownload(scope.$index, scope.row)">下载</el-button>
                    </template>
                </el-table-column>
            </el-table>
        </div>
        <div id="button-group">
            <el-button type="primary" @click="showUploadDialog">上传<i class="el-icon-upload el-icon--right"></i></el-button>
        </div>
        <el-dialog title="上传文件" :visible.sync="uploadDialogVisible">
            <el-upload
            ref="upload"
            action=""
            :http-request="handleFile"
            :multiple="false" 
            :limit="1" 
            :on-exceed="handleExceed"
            :file-list="fileList"
            :auto-upload="true"
            drag>
            <i class="el-icon-upload"></i>
            <div class="el-upload__text">将文件拖到此处，或<em>点击上传</em></div>
            </el-upload>
            <el-progress :percentage=progress id="progress-bar"></el-progress>
            <div slot="footer" class="dialog-footer">
                <el-button @click="uploadDialogVisible = false">取 消</el-button>
                <el-button type="primary" @click="uploadFile">提 交</el-button>
            </div>
        </el-dialog>
    </div>
</div>
</template>

<script>
var docList = []
import navmenu from './nav-menu'
import { convertTimeFormat } from '../../static/js/convertTime'
export default {
    inject: ['reload'],
    name: 'document-manage',
    data() {
        return {
            category: {lastuse:true, create: false, invite: false},
            lastUseTableData: [],
            createTableData: [{docName:'111',lastUseTime:'2019-1-1',shareAmount:10,userData:[{username:'aaaaaaa'},{username:'aaaaaaa'},{username:'aaaaaaa'},{username:'aaaaaaa'},{username:'aaaaaaa'},{username:'aaaaaaa'},{username:'aaaaaaa'},{username:'aaaaaaa'},{username:'aaaaaaa'},]}],
            inviteTableData: [],
            uploadDialogVisible: false,
            fileList: [],
            progress: 0, //进度条显示
            isSuccess: false
        }
    },
    components: { navmenu },
    mounted() {
        (document.getElementById('loading')).style.display = "none"
        this.$axios(
        {
            url:'/document-manage/lastuse',
            method:"post",
            data:{
                username: window.sessionStorage.username,
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
            docList.length = 0
            for(var i = 0; i < response.data.docList.length; i++)
            {
                var docItem = response.data.docList[i]
                docList.push(docItem)
                this.lastUseTableData.push({docName:docItem.docName,owner:docItem.owner,lastUseTime:convertTimeFormat(docItem.lastUseTime),auth:docItem.auth})
            }
            return (response)
        });
    },
    methods: {
        showUploadDialog() {
            for(var i = this.fileList.length-1; i >= 0; i--)
            {
                this.$delete(this.fileList,i)
            }
            this.progress = 0
            this.uploadDialogVisible = true
        },
        changeCategory() {
            var selectitem = event.target.id
            for(var i in this.category) {
                this.category[i] = false;
            }
            this.category[selectitem] = true
            if(selectitem === 'lastuse')
            {
                for(var i = this.lastUseTableData.length-1; i >= 0; i--)
                {
                    this.$delete(this.lastUseTableData,i)
                }
                this.$axios(
                {
                    url:'/document-manage/lastuse',
                    method:"post",
                    data:{
                        username: window.sessionStorage.username,
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
                    docList.length = 0
                    for(var i = 0; i < response.data.docList.length; i++)
                    {
                        var docItem = response.data.docList[i]
                        docList.push(docItem)
                        this.$set(this.lastUseTableData,i,{docName:docItem.docName,owner:docItem.owner,lastUseTime:convertTimeFormat(docItem.lastUseTime),auth:docItem.auth})
                    }
                    return (response)
                });
            }
            if(selectitem === 'create')
            {
                for(var i = this.createTableData.length-1; i >= 0; i--)
                {
                    this.$delete(this.createTableData,i)
                }
                this.$axios(
                {
                    url:'/document-manage/create',
                    method:"post",
                    data:{
                        username: window.sessionStorage.username,
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
                    docList.length = 0
                    for(var i = 0; i < response.data.docList.length; i++)
                    {
                        var docItem = response.data.docList[i]
                        docList.push(docItem)
                        var userData = []
                        for(var j = 0; j < docItem.userList.length; j++)
                        {
                            userData.push({username:docItem.userList[j]})
                        }
                        this.createTableData.push({docName:docItem.docName,lastUseTime:convertTimeFormat(docItem.lastUseTime),shareAmount:docItem.shareAmount,userData:userData})
                    }
                    return (response)
                });
            }
            if(selectitem === 'invite')
            {
                for(var i = this.inviteTableData.length-1; i >= 0; i--)
                {
                    this.$delete(this.inviteTableData,i)
                }
                this.$axios(
                {
                    url:'/document-manage/share',
                    method:"post",
                    data:{
                        username: window.sessionStorage.username,
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
                    docList.length = 0
                    for(var i = 0; i < response.data.docList.length; i++)
                    {
                        var docItem = response.data.docList[i]
                        docList.push(docItem)
                        this.inviteTableData.push({docName:docItem.docName,owner:docItem.owner,lastUseTime:convertTimeFormat(docItem.lastUseTime),auth:docItem.auth})
                    }
                    return (response)
                });
            }
        },
        handleExplore(index, row) {
            console.log(index, row)
            console.log(docList)
            this.$store.commit('setFilePath', {oldPath:docList[index].oldPath, newPath:docList[index].newPath})
            this.$router.push({
                path: '/document-explore'
            })
        },
        handleEdit(index, row) {
            console.log(index, row)
            console.log(docList)
            this.$store.commit('setFilePath', {oldPath:docList[index].oldPath, newPath:docList[index].newPath})
            this.$router.push({
                path: '/document-edit'
            })
        },
        handleDelete(index, row) {
            console.log(index, row)
            console.log(docList)
            this.$axios(
            {
                url:'/delete',
                method:"post", 
                data:{
                    oldPath: docList[index].oldPath,
                    newPath: docList[index].newPath
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
                        message: '删除成功',
                        type: 'success',
                        duration: 2000
                    })
                    this.reload()
                } else if(response.data.message === 'fail')
                {
                    this.$message({
                        message: '删除失败，请重试',
                        type: 'error',
                        duration: 2000
                    })
                }
            });
        },
        handleDownload(index, row) {
            this.$axios(
            {
                url:'/download',
                method:"post",
                data:{
                    path: docList[index].oldPath,
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
                var fileContent = response.data.content
                var filename = response.data.fileName
                const blob = new Blob([fileContent])
                if (window.navigator.msSaveOrOpenBlob) {
                    // 兼容IE10
                    navigator.msSaveBlob(blob, filename)
                } else {
                    //  chrome/firefox
                    let aTag = document.createElement('a')
                    aTag.download = filename
                    aTag.href = URL.createObjectURL(blob)
                    aTag.click()
                    URL.revokeObjectURL(aTag.href)
                }
                return (response)
            });
        },
        handleExceed(files, fileList) {
            this.$message.warning(`最多上传 ${files.length} 个文件`)
        },
        handleFile(response) {
            this.fileList.push(response.file)
            let formData = new FormData()
            // var config = {
            //     onUploadProgress: progressEvent => {
            //         if(progressEvent.lengthComputable)
            //         {
            //             var complete = (progressEvent.loaded / progressEvent.total * 50).toFixed(0)
            //             console.log(complete)
            //             this.progress = parseInt(complete)
            //         }
            //     }
            // }
            formData.append('username',window.sessionStorage.username)
            formData.append('file', this.fileList[0])
            formData.append('fileUploadFileName',this.fileList[0].name)
            this.$axios.post('/upload', formData)
                .then(response => {
                    if(response.data.message === 'success')
                    {
                        this.isSuccess = true
                        this.progress = 100
                    } else if(response.data.message === 'fail')
                    {
                        this.isSuccess = false
                    }
                })
                .catch(function(error) {
                    console.log(error)
                })
        },
        uploadFile() {
            console.log(this.fileList)
            if(this.isSuccess)
            {
                this.$message({
                    message: '上传成功',
                    type: 'success',
                    duration: 2000
                })
            } else
            {
                this.$message({
                    message: '上传失败，请重试',
                    type: 'error',
                    duration: 2000
                })
            }
            this.uploadDialogVisible = false
            for(var i = this.fileList.length-1; i >= 0; i--)
            {
                this.$delete(this.fileList, i);
            }
            this.reload()
        },
        generateType(index) {
            if(index % 5 === 0) {
                return ''
            } else if(index % 5 === 1) {
                return 'success'
            } else if(index % 5 === 2) {
                return 'info'
            } else if(index % 5 === 3) {
                return 'warning'
            } else if(index % 5 === 4) {
                return 'danger'
            }
        }
    }
}
</script>

<style scoped>
body {
    position: absolute;
}
#main {
    position: absolute;
    left: 0;
    top: 60px;
    bottom: 0;
    right: 0;
    background-color: #F7F7F7;
}
#submenu {
    position: relative;
    top: 50px;
    left: 100px;
    width: 400px;
}
#button-group {
    position: absolute;
    top: 50px;
    left: 90%;
}
a{
    text-decoration: none;
    transition-property: color;
    transition-duration: 0.5s;
    transition-timing-function: ease-out;
    color:#A5A5A5;
    font-size:18px;
    margin-left: 30px;
}
a:hover {
    transition-property: color;
    transition-duration: 0.5s;
    transition-timing-function: ease-out;
    color: #555555;
}
.active {
    color: #555555;
}
#create-table {
    position: relative;
    top: 100px;
    left: 15%;
    width: 1100px;
}
#lastuse-table,#invite-table {
    position: relative;
    top: 100px;
    left: 15%;
    width: 950px;
}
#progress-bar {
    width: 52%;
    left: 25%;
    margin-top: 30px;
}
</style>
