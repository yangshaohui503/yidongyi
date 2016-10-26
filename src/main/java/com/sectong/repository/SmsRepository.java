package com.sectong.repository;

import org.springframework.data.repository.CrudRepository;

import com.sectong.domain.Sms;

public interface SmsRepository extends CrudRepository<Sms, Long> {
	
	Sms findFirstByMobileAndVcodeOrderByExpiredDatetimeDesc(String mobile, String vcode);

}
