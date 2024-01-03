package com.orot.menuboss_tv_kr.domain.usecases

import com.orot.menuboss_tv_kr.domain.repository.LocalRepository
import javax.inject.Inject

class GetUpdatedByUuidUseCase @Inject constructor(private val localRepository: LocalRepository) {
    suspend operator fun invoke(): Boolean = localRepository.getUpdatedByUUID()
}
