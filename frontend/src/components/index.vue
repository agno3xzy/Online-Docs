<template>
    <div>
        <div>
            <backgroundimg></backgroundimg>
            <div class="header" ref="header" align="center">
                OnlineDocs
            </div>
            <div class="main" ref="main" align="center">
                <el-form ref="login" :model="login" style="width:250px;margin-top:20px;">
                    <el-form-item>
                        <el-input v-model="login.username" placeholder="用户名" required=true></el-input>
                    </el-form-item>
                    <el-form-item>
                        <el-input v-model="login.password" placeholder="密码" type="password" required=true></el-input>
                    </el-form-item>
                    <el-form-item>
                        <el-button type="primary" style="width:150px;margin-top:15px;" @click="submitForm">登录</el-button>
                    </el-form-item>
                    <el-form-item>
                        <el-button type="text" @click="toRegister" style="margin-top:-10px;">没有账号？马上注册</el-button>
                    </el-form-item>
                </el-form>
            </div>
        </div>
    </div>
</template>

<script>
import { setTimeout } from 'timers';
import backgroundimg from './background-img'
var flag = true
export default {
    name: 'index',
    data() {
        return {
            login: {
                username: '',
                password: ''
            },
        }
    },
    methods: {
        submitForm() {
            this.$axios(
                {
                    url:'/login',
                    method:"post",
                    data:{
                        username: this.login.username,
                        password: this.login.password,
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
                    if(response.data.isValidate === 'true')
                    {
                        window.sessionStorage.setItem('username', this.login.username) //存储用户名
                        window.sessionStorage.setItem('email',response.data.email)
                        this.$router.push({
                            path: '/document-manage'
                        })
                    } else
                    {
                        this.$message({
                            message: '用户名或密码错误',
                            type: 'error',
                            duration: 2000
                        })
                    }
                });
        },
        toRegister() {
            this.$router.push({
                path: '/register'
            })
        },
        handleKeyDown(e) {
            var that = this
            var key = window.event.keyCode ? window.event.keyCode : window.event.which
            if( key === 13 ){
                if(flag)
                {
                    that.submitForm()
                    flag = false
                }
                e.preventDefault()
            }
        },
        handleKeyUp(e) {
            var that = this
            // enter
            var key = window.event.keyCode ? window.event.keyCode : window.event.which
            if( key === 13 ){
                flag = true
                e.preventDefault()
            }
        }
    },
    created() {
        document.addEventListener('keydown', this.handleKeyDown)
        document.addEventListener('keyup', this.handleKeyUp)
    },
    destroyed() {
        document.removeEventListener('keydown', this.handleKeyDown)
        document.removeEventListener('keyup', this.handleKeyUp)
    },
    components: {backgroundimg}
}
</script>

<style scoped>
@import '../../static/css/keyframe.css';
.header {
    position: absolute;
    font-size:40px;
    font-weight: bold;
    left: 50%;
    top: 25%;
    opacity: 0;
    transform: translate(-50%, -25%);
    animation: fadeIn 2s ease 0.5s 1;
    animation-fill-mode: forwards;
}
.main {
    position: absolute;
    left: 50%;
    top: 45%;
    opacity: 0;
    transform: translate(-50%, -30%);
    animation: fadeIn 2s ease 1s 1;
    animation-fill-mode: forwards;

}
</style>