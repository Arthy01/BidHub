package de.hwrberlin.bidhub.util;

import de.hwrberlin.bidhub.model.shared.NetworkResponse;

public class WaitForResponse {
    public WaitForResponse(NetworkResponse response){
        while (!response.hasResponse()){

        }
    }
}
