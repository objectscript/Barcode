# Barcode

Barcode reader. 

# Use

1. Import `isc.barcode`
2. Run:

```
/// Utility methods
Class isc.barcode.Utils
{

/// Main class to import
Parameter CLASS = "isc.barcode";

/// Gateway name to create/use
Parameter GATEWAY = "Java";

/// Get JGW object
ClassMethod connect(gatewayName As %String = {..#GATEWAY}, path As %String = "barcode-1.0-SNAPSHOT-jar-with-dependencies.jar", Output sc As %Status) As %Net.Remote.Gateway
{
	set gateway = ""
	set sc = ##class(%Net.Remote.Service).OpenGateway(gatewayName, .gatewayConfig)
	quit:$$$ISERR(sc) gateway
	set sc = ##class(%Net.Remote.Service).ConnectGateway(gatewayConfig, .gateway, path, $$$YES)
	quit gateway
}

/// Get barcode
/// Write $System.Status.GetErrorText(##class(isc.barcode.Utils).getBarCode())
ClassMethod getBarCode(file As %String, debug As %Boolean = {$$$YES}) As %Status
{
    #dim gateway as %Net.Remote.Gateway
    #dim exception as %Exception.AbstractException

    set sc = $$$OK
    try {
        set gateway = ..connect()
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
```
