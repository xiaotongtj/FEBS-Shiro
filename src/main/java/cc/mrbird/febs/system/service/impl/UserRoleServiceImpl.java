package cc.mrbird.febs.system.service.impl;

import cc.mrbird.febs.system.entity.UserRole;
import cc.mrbird.febs.system.mapper.UserRoleMapper;
import cc.mrbird.febs.system.service.IUserRoleService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * @author MrBird
 * <p>
 * readOnly的意思就是当前的方法是只读的，也就是说当前的方法中没有需要处理事务（insert,update,delete）的操作。
 * 则可以加上readOnly=true
 * 使用它的好处是Spring会把你优化这方法，使用了readOnly=true，也就是使用了一个只读的connection。效率会高很多​
 */
@Service
@Transactional(propagation = Propagation.SUPPORTS, readOnly = true, rollbackFor = Exception.class)
public class UserRoleServiceImpl extends ServiceImpl<UserRoleMapper, UserRole> implements IUserRoleService {

    @Override
    @Transactional
    public void deleteUserRolesByRoleId(List<String> roleIds) {
        this.baseMapper.delete(new QueryWrapper<UserRole>().lambda().in(UserRole::getRoleId, roleIds));
        //this.baseMapper.delete(new LambdaQueryWrapper<UserRole>().in(UserRole::getRoleId, roleIds));
    }

    @Override
    @Transactional
    public void deleteUserRolesByUserId(List<String> userIds) {
        this.baseMapper.delete(new QueryWrapper<UserRole>().lambda().in(UserRole::getUserId, userIds));
    }
}
