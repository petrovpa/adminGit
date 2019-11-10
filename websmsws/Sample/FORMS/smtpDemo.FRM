object NewForm: TWForm
  Left = 0
  Top = 0
  Width = 480
  Height = 500
  BgColor = '#FFFFFF'
  FgColor = '#000000'
  object WLabel1: TWLabel
    Left = 0
    Top = 176
    Width = 145
    Height = 17
    Caption = #1058#1077#1082#1089#1090' '#1089#1086#1086#1073#1097#1077#1085#1080#1103
  end
  object WLabel2: TWLabel
    Left = 0
    Top = 40
    Width = 145
    Height = 17
    Caption = #1040#1076#1088#1077#1089#1072
  end
  object WLabel3: TWLabel
    Left = 0
    Top = 120
    Width = 145
    Height = 17
    Caption = #1047#1072#1075#1086#1083#1086#1074#1086#1082' '#1087#1080#1089#1100#1084#1072
  end
  object WMemo1: TWMemo
    Left = 0
    Top = 192
    Width = 473
    Height = 233
    TabIndex = 0
    Lines.Strings = (
      '')
    Required = False
  end
  object WButton1: TWButton
    Left = 0
    Top = 432
    Width = 75
    Height = 25
    TabIndex = 0
    Caption = #1054#1090#1087#1088#1072#1074#1080#1090#1100
    JSOnClick.Strings = (
      'sendForm("NEXT", true);')
  end
  object Address: TWMemo
    Left = 0
    Top = 64
    Width = 473
    Height = 41
    TabIndex = 0
    Lines.Strings = (
      '')
    Required = False
  end
  object Title: TWEdit
    Left = 0
    Top = 136
    Width = 473
    Height = 18
    TabIndex = 0
    PasswordField = False
    Required = False
    Alignment = taLeftJustify
    MinLength = 0
    InputKind = dfNone
  end
end
