<template>
<div>
    <backgroundimg></backgroundimg>
    <div id="register_form">
        <span class="title">Online Docs</span>
        <el-form :model="form" label-position="right" ref="userForm" size="mini" :rules="rule" label-width="80px">
            <el-form-item label="用户名" prop="username">
                <el-input v-model="form.username" size="mini"></el-input>
            </el-form-item>
            <el-form-item label="密码" prop="password">
                <el-input type="password" v-model="form.password" size="mini"></el-input>
            </el-form-item>
            <el-form-item label="确认密码" prop="confirmPassword">
                <el-input type="password" v-model="form.confirmPassword" size="mini"></el-input>
            </el-form-item>
            <el-form-item label="邮箱" prop="email">
                <el-input v-model="form.email" size="mini"></el-input>
            </el-form-item>
            <el-form-item>
                <el-button type="primary" round @click="createUser" style="width:150px;margin-top:10px;">注册</el-button>
            </el-form-item>
            <el-form-item>
                <el-button type="text" @click="toLogin">已有账号？马上登陆</el-button>
            </el-form-item>
        </el-form>
    </div>
</div>
</template>

<script>
import backgroundimg from './background-img'
export default {
    name: 'register',
    data() {
        var validateUsername = (rule,value,callback) => {
            if(!value)
            {
                return callback(new Error('用户名不能为空'))
            }
            this.$axios(
            {
                url:'/register/checkUsername',
                method:"post",
                data:{
                    username: value
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
                if(response.data.message === 'false')
                {
                    callback(new Error('用户名已存在'))
                } else
                {
                    callback()
                }
            });
        };
        var validatePassword = (rule,value,callback) => {
            if(!value)
            {
                callback (new Error('密码不能为空'))
            } else
            {
                callback()
            }
        };
        var validateConfirmPassword = (rule,value,callback) => {
            if(!value)
            {
                return callback(new Error('确认密码不能为空'))
            }
            if(value !== this.form.password)
            {
                callback(new Error('两次密码不一致'))
            } else
            {
                callback()
            }
        };
        var validateEmail = (rule, value, callback) => {
            if (value === '') {
            callback(new Error('邮箱不能为空'));
            } else {
            if (value !== '') { 
                var reg=/^[A-Za-z0-9\u4e00-\u9fa5]+@[a-zA-Z0-9_-]+(\.[a-zA-Z0-9_-]+)+$/;
                if(!reg.test(value)){
                    callback(new Error('请输入有效的邮箱'));
                }
            }
                callback();
            }
        };
        return {
            form: {
                username: '',
                password: '',
                confirmPassword: '',
                email: ''
            },
            rule: {
                username: [{validator: validateUsername, trigger: 'blur'}],
                password: [
                    {validator:validatePassword, trigger: 'blur'},
                    {min: 6, message: '密码至少6位', trigger: 'blur'}
                ],
                confirmPassword: [{validator: validateConfirmPassword, trigger: 'blur'}],
                email: [{validator: validateEmail, trigger: 'blur'}]
            }
        }
    },
    components: {backgroundimg},
    methods: {
        createUser() {
            this.$refs['userForm'].validate((valid) => {
                if(valid)
                {
                    this.$axios(
                    {
                        url:'/register/create',
                        method:"post",
                        data:{
                            username: this.form.username,
                            password: this.form.password,
                            email: this.form.email
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
                            this.$message({
                                type: 'success',
                                message: '注册成功,返回登陆页面',
                                duration: 2000
                            })
                            this.$router.push({
                                path: '/'
                            })
                        } else
                        {
                            this.$message({
                                type: 'error',
                                message: '注册失败，请重试',
                                duration: 2000
                            })
                        }
                    });
                }
            });
            
        },
        toLogin() {
            this.$router.push({
                path: '/'
            })
        }
    }
}
</script>

<style scoped>
@import '../../static/css/keyframe.css';
.el-form {
    width: 250px;
    position: relative;
    margin-top: 30px;
    margin-left: 10%;
    margin-bottom: 40px;
}
.title {
    display:inline-block;
    margin-top:30px;
    font-size: 25px;
    font-weight: bold;
    color: #4281f5;
}
#register_form {
    position: absolute;
    left: 50%;
    top: 50%;
    transform: translate(-50%,-50%);
    width: 400px;
    background-color: rgba(245,245,245,0.6);
    border-radius: 5px;
    opacity: 0;
    animation: fadeIn 1s ease 0.5s 1;
    animation-fill-mode: forwards;
}
</style>