package com.example.learnkotlin.util

/**
 * Created by libra on 2017/10/30.
 */

interface Constants {
    interface IntentParam {
        companion object {
            const val Title = "title"
            const val Tag = "tag"
            const val Extra = "extra"
            const val Object = "object"
            const val pictype = "pictype"
            const val picIndex = "picInex"
            const val From = "from"
        }
    }

    interface MaxLimit {
        companion object {
            const val Location = 5
            const val Room = 20
            const val Device = 30
            const val Scene = 20
            const val Timer = 20
            const val Hub = 5
            const val UserShared = 5

        }
    }

    interface DeviceType {
        companion object {
            const val WifiCurtain = "22000000" //wifi开合帘
            const val WifiReceiver = "22000001" //wifi电机控制盒(注：暂时弃用，采用22000005)
            const val Host = "02000001" //网关
            const val SubSet = "10000000" //433管状电机
            const val TDBU = "10000001" //TDBU电机 type 9
            const val DoubleRoller = "10000002"//双层罗拉帘 type 5 10 27
            const val WifiTubularMotor = "22000002"//wifi管状电机
            const val WifiModule = "22000005"//新的wifi电机控制盒（接收器）
            const val WifiSwitch = "22000007"//
        }
    }

    interface HostType {
        companion object {
            //deviceSignCode
            const val Default = "DD7002B"
            const val Usb = "DD1554"
            const val Ethernet = "DD7005"
        }
    }

    interface HostVersion {
        companion object {
            const val Default = "0.8.0"   //DD7002B
            const val Usb = "0.8.2.0"     //DD1554
            const val Ethernet = "0.7.2"  //DD7005
        }
    }

    interface DeviceSignCode {
        companion object {
            //2200002
            const val WifiTubular_Auto_Position = "DD1402B"
            //2200005
            const val WifiTubular_Door_Switch = "DC117D"
            const val WifiModule_No_Trip = "DD114S"
        }
    }

    interface CmdName {
        companion object {
            //WifiCurtain:0-关,1-开,2-暂停,3-设置电机换向,11-设置当前位置为第三行程点, 12-运行第三行程点;
            //SubSet:0-下,1-上,2-暂停,3-电机方向切换,4-电机角度方向切换,5-状态查询,6-电量查询,7-点动上,
            // 8-点动下,9-设置当前位置为上行程点,10-设置当前位置为下行程点,11-设置当前位置为第三行程点,
            // 12-运行第三行程点,13-调整上行程,14-调整下行程,15-角度系数加1,16-角度系数减1
            //22000002设备:
            //22000005设备:0-关,1-开,2-暂停,3-设置电机换向,4-自动设置行程;
            const val Operation = "operation"
            const val CurrentPosition = "currentPosition"
            const val TargetPosition = "targetPosition"
            const val Direction = "direction" //0-正常,1-反转,2-未校准

            //Host:1-正常工作,2-对码中;
            //SubSet:0-电机所有行程未设置,1-电机上行程已设置,2-电机下行程已设置,3-电机打开关闭行程已设置,4-电机打开关闭行程和第三行程点已设置
            const val CurrentState = "currentState"
            const val NumberOfDevices = "numberOfDevices"
            const val Type = "type"
            const val CurrentAngle = "currentAngle"
            const val TargetAngle = "targetAngle"
            const val VoltageMode = "voltageMode" //0-市电,1-锂电池
            const val BatteryLevel = "batteryLevel" //电池电压，实际值*100
            const val WirelessMode = "wirelessMode" //0-433单向,1-433双向有行程电机,2-433双向无行程电机,3-433单向有行程电机

            const val ControlMode = "controlMode" //外接开关控制模式

            const val State = "state"//1-添码状态 2-角度参数设置状态 4-角度回差系数设置状态

            const val Rssi = "RSSI"//电机信号强度 范围0~4 0-1格信号 4-满信号

            const val SwitchMode = "switchMode"

            //TDBU
            const val BatteryLevel_B = "batteryLevel_B"
            const val BatteryLevel_T = "batteryLevel_T"
            const val CurrentPosition_B = "currentPosition_B"
            const val CurrentPosition_T = "currentPosition_T"
            const val Exist_SubId = "exist_subid"//0-需要配对第二台 1-不需要配对
            const val TargetPosition_B = "targetPosition_B"
            const val TargetPosition_T = "targetPosition_T"
            const val CurrentState_B = "currentState_B"
            const val CurrentState_T = "currentState_T"
            const val Operation_B = "operation_B"
            const val Operation_T = "operation_T"
        }
    }

    interface CmdData {
        companion object {
            const val Operation_WifiCurtain_Close = 0
            const val Operation_WifiCurtain_Open = 1
            const val Operation_Stop = 2
            const val Operation_Direction_Set = 3
            const val Operation_Limit_Set = 4
            const val Operation_Down = 0
            const val Operation_Up = 1
            const val Operation_Close = 0
            const val Operation_Open = 1
            const val Operation_Angle_Derection_set = 4
            const val Operation_Status_Query = 5
            const val Operation_Battery_Query = 6
            const val Operation_Point_Up = 7
            const val Operation_Point_Down = 8
            const val Operation_Point_Up_Set = 9
            const val Operation_Point_Down_Set = 10
            const val Operation_Point_Third_Set = 11
            const val Operation_Point_Third_Run = 12
            const val Operation_Point_Up_Edit = 13
            const val Operation_Point_Down_Edit = 14
            const val Operation_Angle_Plus_1 = 15
            const val Operation_Angle_Minus_1 = 16
            const val Operation_Clear_Point_All = 17
            const val Operation_Clear_Point_Third = 20
            const val Operation_Angle_Program = 21
            const val Operation_Angle_Set = 22
            const val Operation_Angle_RD_Set = 23
            const val Operation_Remote_Set = 24


            const val Direction_Normal = 0
            const val Direction_Reversal = 1
            const val Direction_UnSet = 2

            const val CurrentState_Work = 0
            const val CurrentState_Matching_Code = 1

            const val CurrentState_Device_All_Point_UnSet = 0
            const val CurrentState_Device_Up_Point_HasSet = 1
            const val CurrentState_Device_Down_Point_HasSet = 2
            const val CurrentState_Device_OpenClose_Point_HasSet = 3
            const val CurrentState_Device_OpenCloseThird_Point_HasSet = 4

            const val VoltageMode_Default = 0
            const val VoltageMode_Lithium_Cell = 1

            const val WirelessMode_One_Way = 0
            const val WirelessMode_Two_Way_Point = 1
            const val WirelessMode_Two_Way_Not_Point = 2
            const val WirelessMode_Other_Point = 3
            const val WirelessMode_Two_Way_Virtual_Point = 4

            const val Type_Roller_Blinds = 1
            const val Type_Venetian_Blinds = 2
            const val Type_Roman_Blinds = 3
            const val Type_Honeycomb_Blinds = 4
            const val Type_ShangriLa_Blinds = 5
            const val Type_Roller_Shutter = 6
            const val Type_Roller_Gate = 7
            const val Type_Awning = 8
            const val Type_TDBU = 9
            const val Type_DayNight_Blinds = 10
            const val Type_Dimming_Blinds = 11
            const val Type_Curtain_Double = 12
            const val Type_Curtain_Left = 13
            const val Type_Curtain_Right = 14
            const val Type_Vertical_Blinds = 15
            const val Type_Window = 16
            const val Type_Vision_Blinds = 17
            const val Type_Folding_Awning = 18
            const val Type_Celling_Curtain = 19
            const val Type_Straight_Drop = 20
            const val Type_Japanese_Blinds = 21
            const val Type_Venetian_Blinds_Tilt_Only = 22
            const val Type_Double_Roller = 27
            const val Type_Pleated_Blinds = 42

            const val ControlMode_Double_Button = 0
            const val ControlMode_Double_Button_Reboundable = 1
            const val ControlMode_DC246 = 2

        }
    }
}
