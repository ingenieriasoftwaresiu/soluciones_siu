/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package co.edu.udea.solucionessiu.exception;

import org.apache.log4j.Logger;



/**
 *
 * @author George
 */
public class GIDaoException extends Exception {
    
    private Logger log = Logger.getLogger(this.getClass());
    
    public GIDaoException() {
        log.error("Ocurrió un error en la aplicación.");
    }

    public GIDaoException(String message) {
        super(message);
        log.info(message);
    }

    public GIDaoException(String message, Throwable cause) {
        super(message, cause);
        log.error(message,cause);
    }

    public GIDaoException(Throwable cause) {
        super(cause);
        log.error(cause.getMessage(),cause);
    }       
        
}
