package com.orot.menuboss_tv.data.mapper

import com.orot.menuboss_tv.data.models.DeviceInfoDTO
import com.orot.menuboss_tv.domain.entities.DeviceInfo
import javax.inject.Inject

class DeviceInfoMapper @Inject constructor() {

    fun mapFromDTO(dto: DeviceInfoDTO): DeviceInfo {
        return DeviceInfo(
            `object` = dto.`object`,
            status = dto.status,
            linkProfile = dto.linkProfile?.let { mapLinkProfile(it) },
            property = dto.property?.let { mapProperty(it) },
        )
    }

    private fun mapLinkProfile(tvDTO: DeviceInfoDTO.LinkProfile): DeviceInfo.LinkProfile {
        return DeviceInfo.LinkProfile(
            qrUrl = tvDTO.qrUrl,
            pinCode = tvDTO.pinCode
        )
    }

    private fun mapGrpc(tvDTO: DeviceInfoDTO.Grpc): DeviceInfo.Grpc {
        return DeviceInfo.Grpc(
            host = tvDTO.host,
            port = tvDTO.port,
        )
    }

    private fun mapProperty(tvDTO: DeviceInfoDTO.Property): DeviceInfo.Property {
        return DeviceInfo.Property(
            name = tvDTO.name,
            accessToken = tvDTO.accessToken,
            screenUrl = tvDTO.screenUrl,
            grpc = tvDTO.grpc?.let { mapGrpc(it) },
        )
    }
}
