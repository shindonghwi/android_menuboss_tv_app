package com.orot.menuboss_tv.domain.usecases

import com.orot.menuboss_tv.domain.repository.LocalRepository
import javax.inject.Inject

class PatchUpdatedByUuidUseCase @Inject constructor(private val localRepository: LocalRepository) {
    suspend operator fun invoke(isUpdated: Boolean) = localRepository.setUpdatedByUUID(isUpdated)
}
