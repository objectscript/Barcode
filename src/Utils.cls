/// Utility methods
Class isc.barcode.Utils
{

/// Main class to import
Parameter JARPATH = "C:\java\barcode.jar";

Parameter CLASS = "isc.barcode";

/// Gateway name to create/use
Parameter GATEWAY = "jtest";

/// Load Jar from path.
/// w ##class(isc.barcode.Utils).UpdateJar()
/// Write $System.Status.GetErrorText(##class(isc.barcode.Utils).UpdateJar())
ClassMethod UpdateJar(gatewayName = {..#GATEWAY}, path As %String = {..#JARPATH})
{
	#dim sc As %Status = $$$OK
	//Set sc = ##class(Ens.Director).StopProduction(, 1)
	quit:$$$ISERR(sc) sc
	
	//Set sc = ##class(%Net.Remote.Service).StopGateway(gatewayName)
	quit:$$$ISERR(sc) sc

	set gateway = ..Connect(gatewayName, path, .sc)
	quit:$$$ISERR(sc) sc

	set sc = gateway.%Import(..#CLASS)
	quit:$$$ISERR(sc) sc
	set:'##class(%Dictionary.CompiledClass).%ExistsId(..#CLASS) sc = $$$ERROR($$$GeneralError, $$$FormatText("Class '%1' does not exist",..#CLASS))
	quit:$$$ISERR(sc) sc
	
	set sc = ##class(%Net.Remote.Service).StopGateway(gatewayName)
	
	//Set sc = ##class(Ens.Director).StartProduction()
	quit sc
}

ClassMethod Connect(gatewayName As %String = {..#GATEWAY}, path As %String = {..#JARPATH}, Output sc As %Status) As %Net.Remote.Gateway
{
	set gateway = ""
	set sc = ##class(%Net.Remote.Service).OpenGateway(gatewayName, .gatewayConfig)
	quit:$$$ISERR(sc) gateway
	set sc = ##class(%Net.Remote.Service).ConnectGateway(gatewayConfig, .gateway, path, $$$YES)
	quit gateway
}

/// set stream = ##class(isc.barcode.Utils).createQRCode("test.png", "Hi, world!", 240, "png")
/// w ##class(isc.barcode.Utils).createQRCode()
ClassMethod createQRCode(fileName As %String, qrCodeText As %String, fileSize As %String, fileType As %String, debug As %Boolean = {$$$YES}) As %GlobalBinaryStream
{
    #dim gateway as %Net.Remote.Gateway
    #dim exception as %Exception.AbstractException
    set sc = $$$OK

#; for test 
#;    set fileName = "C:\java\test.png"
#;    set qrCodeText = "hi"
#;    set fileSize = 240
#;    set fileType = "png"
    
    try {
        set gateway = ..Connect()
        set start = $zh
        
        set reader = ##class(isc.barcode).%New(gateway)
        
        set end1 = $zh
        
        set qrCodeStream = reader.generateQRCode(fileName,qrCodeText,fileSize,fileType) 

#; for test 
#;        set file1 = ##class(%FileBinaryStream).%New()
#;        set file1.Filename = "C:\java\test2.png"
#;        w file1.CopyFromAndSave(qrCodeStream),!
       
		set end2 = $zh
		
		if debug {
			write !,"Init: ",end1-start
			write !,"Reader: ",end2-end1, !
			zw barcodes
		}

        set sc = gateway.%Disconnect()

    } catch ex {
	    break:debug
        set sc = $$$ADDSC(ex.AsStatus(), $g(%objlasterror))
    }

    quit qrCodeStream
}

/// Get barcode
/// Write $System.Status.GetErrorText(##class(isc.barcode.Utils).getBarCode())
ClassMethod getBarCode(file As %String, debug As %Boolean = {$$$YES}) As %Status
{
    #dim gateway as %Net.Remote.Gateway
    #dim exception as %Exception.AbstractException

    set sc = $$$OK
    try {
        set gateway = ..Connect()
        set start = $zh
        
        set reader = ##class(isc.barcode).%New(gateway)
        
        set end1 = $zh
        set barcodes = reader.readBarCode(file) 
		set end2 = $zh
		
		if debug {
			write !,"Init: ",end1-start
			write !,"Reader: ",end2-end1, !
			zw barcodes
	        //break
		}

        set sc = gateway.%Disconnect()

    } catch ex {
	    break:debug
        set sc = $$$ADDSC(ex.AsStatus(), $g(%objlasterror))
    }

    quit sc
}

}

