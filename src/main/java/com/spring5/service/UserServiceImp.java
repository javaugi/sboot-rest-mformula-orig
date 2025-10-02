package com.spring5.service;

import com.spring5.dao.UserDao;
import com.spring5.entity.User;
import com.spring5.repository.UserRepository;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
public class UserServiceImp extends UserServiceAbstractImp {

	private final UserDao userDao;

	private final UserRepository repo;

	private final PasswordEncoder encoder;

	@Transactional
	public User register(User user) {
		user.setPassword(encoder.encode(user.getPassword()));
		return repo.save(user);
	}

	@Override
	public boolean validateUserExists(Long id) {
		return repo.findById(id).isPresent();
	}

	@Transactional
	@Override
	public void save(User user) {
		userDao.save(user);
	}

	@Transactional
	@Override
	public void saveAll(List<User> users) {
		userDao.saveAll(users);
	}

	@Transactional(readOnly = true)
	@Override
	public Optional<User> findById(Long id) {
		return userDao.findById(id);
	}

	@Transactional(readOnly = true)
	@Override
	public Iterable<User> findAll() {
		return userDao.findAll();
	}

	@Transactional(readOnly = true)
	@Override
	public Iterable<User> findAll(int offset, int limit) {
		return userDao.findAll(offset, limit);
	}

	@Transactional(readOnly = true)
	@Override
	public Iterable<User> findAll(Sort sort) {
		return userDao.findAll();
	}

	@Transactional(readOnly = true)
	@Override
	public Page<User> findAll(Pageable pageable) {
		return userDao.findAll(pageable);
	}

}
